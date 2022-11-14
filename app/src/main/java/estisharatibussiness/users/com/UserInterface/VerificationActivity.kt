package estisharatibussiness.users.com.UserInterface

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import estisharatibussiness.users.com.R
import android.widget.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import estisharatibussiness.users.com.ApiHelper.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelper.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelper.Login.DataUser
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.Utils.BaseCompatActivity
import estisharatibussiness.users.com.Utils.PinOnKeyListener
import estisharatibussiness.users.com.Utils.PinTextWatcher
import kotlinx.android.synthetic.main.activity_verification.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit


class VerificationActivity : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var preferencesHelper: SharedPreferencesHelper

    var vrCode: String? = null
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
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
        helperMethods = HelperMethods(this@VerificationActivity)
        preferencesHelper = SharedPreferencesHelper(this@VerificationActivity)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.setLanguageCode(preferencesHelper.appLang)
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
          helperMethods.dismissProgressDialog()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.d("FirebaseException", e.message!!)
                    helperMethods.showToastMessage("" + e.message)
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.d("FirebaseException", e.message!!)
                    helperMethods.showToastMessage("" + e.message)
                }

                helperMethods.dismissProgressDialog()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("onCodeSent", verificationId)
                vrCode = verificationId
                helperMethods.showToastMessage(getString(R.string.code_send_successfully))
                ResendCountdown()
                helperMethods.dismissProgressDialog()
            }
        }

        if (intent.extras != null) {
            come_from = intent.getStringExtra("come_from")!!
            phone = intent.getStringExtra("phone")!!
            email = intent.getStringExtra("email")!!
            if (come_from.equals("Registration")) {
                password = intent.getStringExtra("password")!!
                verified = intent.getStringExtra("verified")!!
                if (verified.equals("false")) {
                    sendCodeFromFirebase(phone)
                } else {
                    sendCodeFromFirebase(phone)
                }
            }
            Log.d("come_from", come_from + "  " + phone + "   " + verified)
        }
    }

    fun clickEvents() {
        nav_back.setOnClickListener {
            finish()
        }
        val editTexts = arrayOf(code_1, code_2, code_3, code_4 ,code_5, code_6)

        code_1.addTextChangedListener(PinTextWatcher(this@VerificationActivity, 0, editTexts))
        code_2.addTextChangedListener(PinTextWatcher(this@VerificationActivity, 1, editTexts))
        code_3.addTextChangedListener(PinTextWatcher(this@VerificationActivity, 2, editTexts))
        code_4.addTextChangedListener(PinTextWatcher(this@VerificationActivity, 3, editTexts))
        code_5.addTextChangedListener(PinTextWatcher(this@VerificationActivity, 4, editTexts))
        code_6.addTextChangedListener(PinTextWatcher(this@VerificationActivity, 5, editTexts))

        code_1.setOnKeyListener(PinOnKeyListener(this@VerificationActivity, 0, editTexts))
        code_2.setOnKeyListener(PinOnKeyListener(this@VerificationActivity, 1, editTexts))
        code_3.setOnKeyListener(PinOnKeyListener(this@VerificationActivity, 2, editTexts))
        code_4.setOnKeyListener(PinOnKeyListener(this@VerificationActivity, 3, editTexts))
        code_5.setOnKeyListener(PinOnKeyListener(this@VerificationActivity, 4, editTexts))
        code_6.setOnKeyListener(PinOnKeyListener(this@VerificationActivity, 5, editTexts))

        btn_proceed.setOnClickListener {

            if (codeValidation()) {
                if (vrCode == null) {
                    helperMethods.showToastMessage(getString(R.string.please_wait_while_send_code))
                    return@setOnClickListener
                }
                val code = "${code_1.toText()}${code_2.toText()}${code_3.toText()}${code_4.toText()}${code_5.toText()}${code_6.toText()}"
                val credential = PhoneAuthProvider.getCredential(vrCode!!, code)
                signInWithPhoneAuthCredential(credential)
            }
        }

        retry.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                sendCodeFromFirebase(phone)
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

    private fun sendCodeFromFirebase(phone:String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phone) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                helperMethods.showToastMessage(getString(R.string.code_verified))
                verifyPhoneApiCall(phone, "")
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    helperMethods.showToastMessage(getString(R.string.invalid_verification_code))
                } else {
                    helperMethods.showToastMessage(getString(R.string.verification_failed))
                }
            }
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

                                val intent = Intent(this@VerificationActivity, OnBoarding::class.java)
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
