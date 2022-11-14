package estisharatibussiness.users.com.UserInterface

import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import estisharatibussiness.users.com.ApiHelper.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelper.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelper.Login.DataUser
import estisharatibussiness.users.com.DataClassHelper.Pages.PageResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_pages.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ActivityPages : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityPages)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ActivityPages)
        dataUser = sharedPreferencesHelper.logInUser
        intent.getStringExtra("page")?.let { pagesApiCall(it) }
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun pagesApiCall(pageLine: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.PAGES_API_CALL("Bearer ${dataUser.access_token}", pageLine)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val page = jsonObject.getString("page")
                                val pageResponse: PageResponse = Gson().fromJson(page, PageResponse::class.java)
                                actionBarTitle.text = pageResponse.name
                                content.text = helperMethods.getHtmlText(pageResponse.content)
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
