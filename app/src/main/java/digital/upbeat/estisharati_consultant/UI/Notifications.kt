package digital.upbeat.estisharati_consultant.UI

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_consultant.Adapter.NotificationsAdapter
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_consultant.DataClassHelper.Notification.NotificationsResponse
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_notifications.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Notifications : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        initViews()
        clickEvents()
        callNotificationAPI()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Notifications)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@Notifications)
        dataUser = sharedPreferencesHelper.logInConsultant
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        clearAll.setOnClickListener { callClearNotificationAPI() }
    }

    fun InitializeRecyclerview() {
        notificationsRecycler.setHasFixedSize(true)
        notificationsRecycler.removeAllViews()
        notificationsRecycler.layoutManager = LinearLayoutManager(this@Notifications)
        notificationsRecycler.adapter = NotificationsAdapter(this@Notifications, this@Notifications, GlobalData.notificationResponse.data)
        GlobalData.mySubscriberResponse.notification_count = GlobalData.notificationResponse.data.size.toString()
        if (GlobalData.notificationResponse.data.size > 0) {
            notificationsRecycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            notificationsRecycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
            errorText.text = getString(R.string.there_is_no_notification_available)
        }
    }

    private fun callNotificationAPI() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.NOTIFICATION_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                           GlobalData. notificationResponse = Gson().fromJson(response.body()!!.string(), NotificationsResponse::class.java)
                            if (GlobalData.notificationResponse.status.equals("200")) {
                                InitializeRecyclerview()
                                GlobalData.mySubscriberResponse.notification_count="0"
                            } else {
                               if (helperMethods.checkTokenValidation(GlobalData.notificationResponse.status, GlobalData.notificationResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), GlobalData.notificationResponse.message)
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
    private fun callClearNotificationAPI() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.CLEAR_NOTIFICATIONS_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                           GlobalData. notificationResponse = Gson().fromJson(response.body()!!.string(), NotificationsResponse::class.java)
                            if (GlobalData.notificationResponse.status.equals("200")) {
                                InitializeRecyclerview()
                                GlobalData.mySubscriberResponse.notification_count="0"
                            } else {
                               if (helperMethods.checkTokenValidation(GlobalData.notificationResponse.status, GlobalData.notificationResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), GlobalData.notificationResponse.message)
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