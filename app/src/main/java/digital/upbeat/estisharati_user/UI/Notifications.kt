package digital.upbeat.estisharati_user.UI

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.NotificationsAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Notification.NotificationResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_notifications.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Notifications : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    var notificationResponse: NotificationResponse = NotificationResponse("", "", arrayListOf())
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
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        clearAll.setOnClickListener { callClearNotificationAPI() }
    }

    fun InitializeRecyclerview() {
        notificationsRecycler.setHasFixedSize(true)
        notificationsRecycler.removeAllViews()
        notificationsRecycler.layoutManager = LinearLayoutManager(this@Notifications)
        notificationsRecycler.adapter = NotificationsAdapter(this@Notifications, this@Notifications, notificationResponse.data)

        if (notificationResponse.data.size > 0) {
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
                            notificationResponse = Gson().fromJson(response.body()!!.string(), NotificationResponse::class.java)
                            if (notificationResponse.status.equals("200")) {
                                InitializeRecyclerview()
                                if (GlobalData.isThingInitialized()) {
                                    GlobalData.homeResponse.notification_count = "0"
                                }
                            } else {
                                if (helperMethods.checkTokenValidation(notificationResponse.status, notificationResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), notificationResponse.message)
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
                            notificationResponse = Gson().fromJson(response.body()!!.string(), NotificationResponse::class.java)
                            if (notificationResponse.status.equals("200")) {
                                InitializeRecyclerview()
                                if (GlobalData.isThingInitialized()) {
                                    GlobalData.homeResponse.notification_count = "0"
                                }
                                helperMethods.showToastMessage(notificationResponse.message)
                            } else {
                                if (helperMethods.checkTokenValidation(notificationResponse.status, notificationResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), notificationResponse.message)
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