package estisharatibussiness.users.com.UserInterface

import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import estisharatibussiness.users.com.Adapter.BlogAdapter
import estisharatibussiness.users.com.ApiHelper.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelper.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelper.Login.DataUser
import estisharatibussiness.users.com.DataClassHelper.Blog.Data
import estisharatibussiness.users.com.DataClassHelper.Blog.BlogResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import kotlinx.android.synthetic.main.activity_blog.*
import kotlinx.android.synthetic.main.activity_blog.emptyLayout
import kotlinx.android.synthetic.main.activity_blog.errorText
import kotlinx.android.synthetic.main.activity_blog.filter_spinner
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ActivityBlogActivity : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    var blogArrayList: ArrayList<Data> = arrayListOf()
    var sortingOptionsArraylist: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            postApiCall(filter_spinner.getItemAtPosition(0).toString())
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        sortingOptionsArraylist= arrayListOf(getString(R.string.newest),getString(R.string.oldest))
        initializeFilterSpinner()
        helperMethods = HelperMethods(this@ActivityBlogActivity)
        preferencesHelper = SharedPreferencesHelper(this@ActivityBlogActivity)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }
    fun initializeFilterSpinner() {
        val typeface = ResourcesCompat.getFont(this@ActivityBlogActivity, R.font.almarai_regular)
        val adapter = ArrayAdapter(this@ActivityBlogActivity, R.layout.support_simple_spinner_dropdown_item, sortingOptionsArraylist)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        filter_spinner.adapter = adapter
        filter_spinner.setSelection(0, true)
        val v2 = filter_spinner.selectedView
        (v2 as TextView).textSize = 13f
        v2.typeface = typeface
        v2.setTextColor(ContextCompat.getColor(this@ActivityBlogActivity, R.color.white))
        filter_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                blogArrayList.clear()
                (view as TextView).textSize = 13f
                view.typeface = typeface
                view.setTextColor(ContextCompat.getColor(this@ActivityBlogActivity, R.color.white))
                postApiCall(sortingOptionsArraylist[filter_spinner.selectedItemPosition])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }


    fun InitializeRecyclerview() {
        blogRecycler.setHasFixedSize(true)
        blogRecycler.removeAllViews()
        blogRecycler.layoutManager = LinearLayoutManager(this@ActivityBlogActivity)
        blogRecycler.adapter = BlogAdapter(this@ActivityBlogActivity, this@ActivityBlogActivity, blogArrayList)

        if (blogArrayList.size > 0) {
            emptyLayout.visibility = View.GONE
            blogRecycler.visibility = View.VISIBLE
        } else {
            errorText.text = getString(R.string.there_is_no_blog_post_available)
            emptyLayout.visibility = View.VISIBLE
            blogRecycler.visibility = View.GONE
        }
    }

    fun postApiCall(sorting:String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.POSTS_API_CALL("Bearer ${dataUser.access_token}",sorting)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val blogResponse: BlogResponse = Gson().fromJson(response.body()!!.string(), BlogResponse::class.java)
                            if (blogResponse.status.equals("200")) {
                                blogArrayList = blogResponse.data
                                InitializeRecyclerview()
                            } else {
                                if (helperMethods.checkTokenValidation(blogResponse.status, blogResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), blogResponse.message)
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
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }
}