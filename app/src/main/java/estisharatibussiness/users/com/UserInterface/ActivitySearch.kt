package estisharatibussiness.users.com.UserInterface

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import estisharatibussiness.users.com.Adapter.SearchAdapter
import estisharatibussiness.users.com.ApiHelper.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelper.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelper.Login.DataUser
import estisharatibussiness.users.com.DataClassHelper.Search.SearchResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ActivitySearch : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser

    data class DataSearch(val id: String, val name: String, val type: String)

    val baseSearchArrayList: ArrayList<DataSearch> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()
        clickEvents()

        if (helperMethods.isConnectingToInternet) {
            searchApiCall("")
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivitySearch)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ActivitySearch)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener {
            finish()
        }
        searchEditFrame.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                InitializeRecyclerview()
            }
        })
    }

    fun InitializeRecyclerview() {
        val dataSearchArrayList: ArrayList<DataSearch> = arrayListOf()
        for (data in baseSearchArrayList) {
            if (searchEditFrame.text.toString().equals("")) {
                dataSearchArrayList.add(data)
            } else if (data.name.contains(searchEditFrame.text.toString(), true)) {
                dataSearchArrayList.add(data)
            }
        }
        if (dataSearchArrayList.size > 0) {
            searchRecycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            searchRecycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
            errorText.text = getString(R.string.there_is_no_cources_or_consultaions_found)
        }

        searchRecycler.setHasFixedSize(true)
        searchRecycler.removeAllViews()
        searchRecycler.layoutManager = LinearLayoutManager(this@ActivitySearch)
        searchRecycler.adapter = SearchAdapter(this@ActivitySearch, this@ActivitySearch, dataSearchArrayList)
    }

    fun searchApiCall(searchTxt: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.SEARCH_API_CALL("Bearer ${dataUser.access_token}", searchTxt)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val dataString = jsonObject.getString("data")
                                val searchResponse = Gson().fromJson(dataString, SearchResponse::class.java)
                                for (consultation in searchResponse.consultations) {
                                    baseSearchArrayList.add(DataSearch(consultation.id, consultation.name, "consultation"))
                                }
                                for (course in searchResponse.courses) {
                                    baseSearchArrayList.add(DataSearch(course.id, course.name, "course"))
                                }
                                InitializeRecyclerview()
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
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }
}