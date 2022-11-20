package estisharatibussiness.users.com.CommonApiHelperClasses

import android.content.Context
import android.util.Log
import android.widget.Toast
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivitySplashScreen
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SendDeviceTokenHelper(val context: Context, val activitySplashScreen: ActivitySplashScreen?, val IfNotService: Boolean) {
    var helperMethods: HelperMethods
    var retrofitInterface: RetrofitInterface
    var preferencesHelper: SharedPreferencesHelper

    init {
        helperMethods = HelperMethods(this.context)
        preferencesHelper = SharedPreferencesHelper(this.context)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
    }

    fun SendDeviceTokenFirebase() {
        if (!preferencesHelper.isUserLogIn) {
            return
        }
        if (IfNotService) {
            helperMethods.showProgressDialog(context.getString(R.string.Please_wait))
        }
        if (GlobalData.FcmToken.equals("")) {
            helperMethods.dismissProgressDialog()
            Toast.makeText(context, context.getString(R.string.could_not_get_your_device_token_please_try_again), Toast.LENGTH_LONG).show()
            if (activitySplashScreen != null) {
                activitySplashScreen.checkSelfPermission()
            }

            return
        }
        val responseBodyCall = retrofitInterface.UPDATE_FCM_API_CALL("Bearer ${preferencesHelper.logInUser.access_token}", GlobalData.FcmToken)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val user = jsonObject.getString("user")
                            } else {
                                val message = jsonObject.optString("message")
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    return
                                }
                                helperMethods.showToastMessage(message)
                            }
                        } catch (e: JSONException) {

                            helperMethods.showToastMessage(context.getString(R.string.something_went_wrong_on_backend_server))

                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("body", "Body Empty")
                    }
                } else {
                    helperMethods.showToastMessage(context.getString(R.string.something_went_wrong))
                    Log.d("body", "Not Successful")
                }
                if (activitySplashScreen != null) {
                    activitySplashScreen.checkSelfPermission()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                //                Toast.makeText(context, context.getString(R.string.your_network_connection_is_slow_please_try_again), Toast.LENGTH_LONG).show()
                if (activitySplashScreen != null) {
                    activitySplashScreen.checkSelfPermission()
                }
            }
        })
    }
}
