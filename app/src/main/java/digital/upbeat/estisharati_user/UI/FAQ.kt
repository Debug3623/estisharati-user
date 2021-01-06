package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.FaqAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.FaqDetails.Data
import digital.upbeat.estisharati_user.DataClassHelper.FaqDetails.FAQResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_faq.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FAQ : BaseCompatActivity() {

    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    var faqResponse : FAQResponse = FAQResponse(arrayListOf(), "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        initViews()
    }

    private fun initViews() {
        helperMethods = HelperMethods(this@FAQ)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@FAQ)
        dataUser = sharedPreferencesHelper.logInUser

        if (helperMethods.isConnectingToInternet) {
            FAQ_API_CALL()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }

        nav_Faqback.setOnClickListener {
            finish()
        }
    }

    private fun initializeFAQRecyclerview() {
        faq_recycler.setHasFixedSize(true)
        faq_recycler.removeAllViews()
        faq_recycler.layoutManager = LinearLayoutManager(this@FAQ)
        faq_recycler.adapter = FaqAdapter(this@FAQ,faqResponse.data)
    }

    fun FAQ_API_CALL() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.FAQ_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            faqResponse = Gson().fromJson(response.body()!!.string(), FAQResponse::class.java)
                            if (faqResponse.status.equals("200")) {
                                initializeFAQRecyclerview()
                            } else {
                                val jsonObject = JSONObject(response.body()!!.string())
                                val message = jsonObject.getString("message")
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