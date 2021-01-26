package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import digital.upbeat.estisharati_user.Utils.PinOnKeyListener
import digital.upbeat.estisharati_user.Utils.PinTextWatcher
import kotlinx.android.synthetic.main.activity_verification.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Verification : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var preferencesHelper: SharedPreferencesHelper
    var come_from = ""
    var phone = ""
    var email = ""
    var password = ""
    var verified = ""
    var resendTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Verification)
        preferencesHelper = SharedPreferencesHelper(this@Verification)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)

        if (intent.extras != null) {
            come_from = intent.getStringExtra("come_from")!!
            phone = intent.getStringExtra("phone")!!
            email = intent.getStringExtra("email")!!
            if (come_from.equals("Registration")) {
                password = intent.getStringExtra("password")!!
                verified = intent.getStringExtra("verified")!!
                if (verified.equals("false")) {
                    resendApiCall(phone)
                } else {
                    ResendCountdown()
                }
            }
            Log.d("come_from", come_from + "  " + phone + "   " + verified)
        }
    }

    fun clickEvents() {
        nav_back.setOnClickListener {
            finish()
        }
        val editTexts = arrayOf(code_1, code_2, code_3, code_4)

        code_1.addTextChangedListener(PinTextWatcher(this@Verification, 0, editTexts))
        code_2.addTextChangedListener(PinTextWatcher(this@Verification, 1, editTexts))
        code_3.addTextChangedListener(PinTextWatcher(this@Verification, 2, editTexts))
        code_4.addTextChangedListener(PinTextWatcher(this@Verification, 3, editTexts))

        code_1.setOnKeyListener(PinOnKeyListener(this@Verification, 0, editTexts))
        code_2.setOnKeyListener(PinOnKeyListener(this@Verification, 1, editTexts))
        code_3.setOnKeyListener(PinOnKeyListener(this@Verification, 2, editTexts))
        code_4.setOnKeyListener(PinOnKeyListener(this@Verification, 3, editTexts))

        btn_proceed.setOnClickListener {

            if (codeValidation()) {
                val code = "${code_1.toText()}${code_2.toText()}${code_3.toText()}${code_4.toText()}"
                verifyPhoneApiCall(phone, code)
            }
        }

        retry.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                resendApiCall(phone)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
    }

    fun codeValidation(): Boolean {
        val code = "${code_1.toText()}${code_2.toText()}${code_3.toText()}${code_4.toText()}"

        if (code.length != 4) {
            helperMethods.showToastMessage(getString(R.string.enter_valid_code))
            return false
        }

        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun ResendCountdown() {
        retry.visibility = View.GONE
        retry_on.visibility = View.VISIBLE
        if (resendTimer != null) {
            resendTimer?.cancel()
        }
        resendTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                retry_on.text = getString(R.string.retry_on) + " "+helperMethods.MillisUntilToTime(millisUntilFinished)
            }

            override fun onFinish() {
                retry.visibility = View.VISIBLE
                retry_on.visibility = View.GONE
            }
        }
        resendTimer?.start()
    }

    override fun onStop() {
        super.onStop()
        if (resendTimer != null) {
            resendTimer?.cancel()
        }
    }

    fun resendApiCall(phone: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_resending_code))
        val responseBodyCall = retrofitInterface.RESEND_CODE_API_CALL(phone)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
                                ResendCountdown()
                            } else {
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
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

    fun verifyPhoneApiCall(phone: String, code: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_verifying_code))
        val responseBodyCall = retrofitInterface.VERIFY_PHONE_API_CALL(phone, code)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
                                logInApiCall(email,password)
                            } else {
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

    fun logInApiCall(userIdStr: String, password: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_login_in))
        val responseBodyCall = retrofitInterface.LOGIN_API_CALL(userIdStr, password,"on",GlobalData.FcmToken,"User")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val userString = jsonObject.getString("user")

                                val dataUser = Gson().fromJson(userString, DataUser::class.java)
                                preferencesHelper.isUserLogIn = true
                                preferencesHelper.logInUser=dataUser
                                val hashMap = hashMapOf<String, Any>()
                                hashMap.put("user_id", dataUser.id)
                                hashMap.put("fname", dataUser.fname)
                                hashMap.put("lname", dataUser.lname)
                                hashMap.put("email", dataUser.email)
                                hashMap.put("phone", dataUser.phone)
                                hashMap.put("image", dataUser.image)
                                hashMap.put("fire_base_token", dataUser.user_metas.fire_base_token)
                                hashMap.put("user_type", "user")
                                hashMap.put("online_status", true)
                                hashMap.put("last_seen", FieldValue.serverTimestamp())
                                hashMap.put("availability", true)
                                hashMap.put("channel_unique_id", "")
                                helperMethods.setUserDetailsToFirestore( dataUser.id, hashMap)

                                val intent = Intent(this@Verification, OnBoarding::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
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

    fun EditText.toText(): String = text.toString().trim()
}
