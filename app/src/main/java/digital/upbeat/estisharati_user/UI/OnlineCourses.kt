package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.OnlineCoursesAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.OnlineCourses.DataCategories
import digital.upbeat.estisharati_user.DataClassHelper.OnlineCourses.DataOnlineCourses
import digital.upbeat.estisharati_user.DataClassHelper.OnlineCourses.OnlineCoursesResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_online_courses.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class OnlineCourses : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    var onlineCoursesArrayList: ArrayList<DataOnlineCourses> = arrayListOf()
    var categoriesArrayList: ArrayList<DataCategories> = arrayListOf()
    var sortingOptionsArraylist: ArrayList<String> = arrayListOf()
    var layoutManager: LinearLayoutManager? = null
    var onlineCoursesAdapter: OnlineCoursesAdapter? = null
    var viewState: Parcelable? = null
    var loading = false
    var pageCount = 1
    var totalRecords = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_courses)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            onlineCoursesApiCall("", "", pageCount.toString())
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@OnlineCourses)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@OnlineCourses)
        dataUser = sharedPreferencesHelper.logInUser
        if (GlobalData.isThingInitialized()) {
            notificationCount.text = GlobalData.homeResponse.notification_count
        }
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        notificationLayout.setOnClickListener {
            startActivity(Intent(this@OnlineCourses, Notifications::class.java))
        }

        online_courses_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val visibleItemCount = layoutManager!!.getChildCount()
                    val totalItemCount = layoutManager!!.getItemCount()
                    val pastVisiblesItems = layoutManager!!.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            if (totalItemCount != totalRecords) {
                                if (pageCount > 1) {
                                    circleProgress.visibility = View.VISIBLE
                                }
                                loading = false
                                pageCount++
                                onlineCoursesApiCall(categoriesArrayList.get(categories_spinner.selectedItemPosition).id, sortingOptionsArraylist.get(filter_spinner.selectedItemPosition), pageCount.toString())
                            } else {
                                loading = false
                            }
                        }
                    }
                }
            }
        })
    }

    fun initializeRecyclerview() {
        onlineCoursesAdapter?.let {
            online_courses_recycler.layoutManager?.let {
                viewState = it.onSaveInstanceState()
            }
        }
        online_courses_recycler.setHasFixedSize(true)
        online_courses_recycler.removeAllViews()
        layoutManager = LinearLayoutManager(this@OnlineCourses)
        online_courses_recycler.layoutManager = layoutManager
        onlineCoursesAdapter = OnlineCoursesAdapter(this@OnlineCourses, this@OnlineCourses, onlineCoursesArrayList)
        online_courses_recycler.adapter = onlineCoursesAdapter
        viewState?.let {
            online_courses_recycler.layoutManager?.onRestoreInstanceState(viewState)
        }
        if (onlineCoursesArrayList.size > 0) {
            onlineCoursesLayout.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.VISIBLE
            onlineCoursesLayout.visibility = View.GONE
            errorText.text = getString(R.string.there_is_no_online_courses_found)
        }
    }

    fun initializeCategorySpinner() {
        val arrayList: ArrayList<String> = arrayListOf()
        for (cat in categoriesArrayList) {
            arrayList.add(cat.name)
        }
        val typeface = ResourcesCompat.getFont(this@OnlineCourses, R.font.almarai_regular)
        val adapter = ArrayAdapter(this@OnlineCourses, R.layout.support_simple_spinner_dropdown_item, arrayList)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        categories_spinner.adapter = adapter
        categories_spinner.setSelection(0, true)
        val v2 = categories_spinner.selectedView
        (v2 as TextView).textSize = 13f
        v2.typeface = typeface
        v2.setTextColor(ContextCompat.getColor(this@OnlineCourses, R.color.white))
        categories_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                onlineCoursesArrayList.clear()
                pageCount = 1
                (view as TextView).textSize = 13f
                view.typeface = typeface
                view.setTextColor(ContextCompat.getColor(this@OnlineCourses, R.color.white))
                onlineCoursesApiCall(categoriesArrayList.get(categories_spinner.selectedItemPosition).id, sortingOptionsArraylist.get(filter_spinner.selectedItemPosition), pageCount.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun initializeFilterSpinner() {
        val typeface = ResourcesCompat.getFont(this@OnlineCourses, R.font.almarai_regular)
        val adapter = ArrayAdapter(this@OnlineCourses, R.layout.support_simple_spinner_dropdown_item, sortingOptionsArraylist)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        filter_spinner.adapter = adapter
        filter_spinner.setSelection(0, true)
        val v2 = filter_spinner.selectedView
        (v2 as TextView).textSize = 13f
        v2.typeface = typeface
        v2.setTextColor(ContextCompat.getColor(this@OnlineCourses, R.color.white))
        filter_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                onlineCoursesArrayList.clear()
                pageCount = 1
                (view as TextView).textSize = 13f
                view.typeface = typeface
                view.setTextColor(ContextCompat.getColor(this@OnlineCourses, R.color.white))
                onlineCoursesApiCall(categoriesArrayList.get(categories_spinner.selectedItemPosition).id, sortingOptionsArraylist.get(filter_spinner.selectedItemPosition), pageCount.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun onlineCoursesApiCall(category_id: String, sortby: String, page: String) {
        Log.d("pageCount", page);
        if (pageCount == 1) {
            helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        }
        val responseBodyCall = retrofitInterface.COURSES_API_CALL("Bearer ${dataUser.access_token}", category_id, sortby, page)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                loading = true
                circleProgress.visibility = View.GONE

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val dataString = jsonObject.getString("data")
                                val onlineCoursesResponse = Gson().fromJson(dataString, OnlineCoursesResponse::class.java)
                                totalRecords = onlineCoursesResponse.total_records
                                onlineCoursesArrayList.addAll(onlineCoursesResponse.courses)

                                if (categoriesArrayList.isEmpty()) {
                                    categoriesArrayList = onlineCoursesResponse.categories
                                    initializeCategorySpinner()
                                }
                                if (sortingOptionsArraylist.isEmpty()) {
                                    sortingOptionsArraylist = onlineCoursesResponse.sorting_options
                                    initializeFilterSpinner()
                                }
                                initializeRecyclerview()
                            } else {
                                val message = jsonObject.getString("message")
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), message)
                            }
                        } catch (e: JSONException) {
                            helperMethods.showToastMessage(getString(R.string.something_went_wrong_on_backend_server))
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("body", "Body Empty")
                    }
                } else {
                    helperMethods.showToastMessage(getString(R.string.something_went_wrong))
                    Log.d("body", "Not Successful")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                loading = true
                circleProgress.visibility = View.GONE
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }
}
