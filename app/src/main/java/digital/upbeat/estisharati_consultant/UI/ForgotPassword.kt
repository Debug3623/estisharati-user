package digital.upbeat.estisharati_consultant.UI

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.Utils.BaseCompatActivity
import digital.upbeat.estisharati_consultant.Utils.PinOnKeyListener
import digital.upbeat.estisharati_consultant.Utils.PinTextWatcher
import kotlinx.android.synthetic.main.activity_forgot_password.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class ForgotPassword : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var preferencesHelper: SharedPreferencesHelper
    var resendTimer: CountDownTimer? = null
    var vrCode: String? = null
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ForgotPassword);
        preferencesHelper = SharedPreferencesHelper(this@ForgotPassword)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)

        helperMethods.setStatusBarColor(this, R.color.white)
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

                helperMethods.setStatusBarColor(this@ForgotPassword, R.color.orange)
                forgot_password_layout.visibility = View.GONE
                verification_layout.visibility = View.VISIBLE
                helperMethods.dismissProgressDialog()
            }
        }
    }

    fun clickEvents() {
        val editTexts = arrayOf(code_1, code_2, code_3, code_4, code_5, code_6)

        code_1.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 0, editTexts))
        code_2.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 1, editTexts))
        code_3.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 2, editTexts))
        code_4.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 3, editTexts))
        code_5.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 4, editTexts))
        code_6.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 5, editTexts))

        code_1.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 0, editTexts))
        code_2.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 1, editTexts))
        code_3.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 2, editTexts))
        code_4.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 3, editTexts))
        code_5.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 4, editTexts))
        code_6.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 5, editTexts))


        nav_back_forgot_password.setOnClickListener { finish() }
        nav_back_verification.setOnClickListener {
            forgot_password_layout.visibility = View.VISIBLE
            verification_layout.visibility = View.GONE
            helperMethods.setStatusBarColor(this, R.color.white)
        }
        nav_back_change_password.setOnClickListener {
            verification_layout.visibility = View.VISIBLE
            change_password_layot.visibility = View.GONE
            helperMethods.setStatusBarColor(this, R.color.orange)
        }
        btn_nav_login.setOnClickListener { finish() }
        send_code.setOnClickListener {
            if (forgotPasswordValidation()) {
                forgotPasswordApiCall(codePicker.selectedCountryCodeWithPlus + "" + phone.toText())
                code_1.text = "".toEditable()
                code_2.text = "".toEditable()
                code_3.text = "".toEditable()
                code_4.text = "".toEditable()
                code_5.text = "".toEditable()
                code_6.text = "".toEditable()
            }
        }
        btn_proceed.setOnClickListener {
            if (codeValidation()) {
                val code = "${code_1.toText()}${code_2.toText()}${code_3.toText()}${code_4.toText()}${code_5.toText()}${code_6.toText()}"
                if (vrCode == null) {
                    helperMethods.showToastMessage(getString(R.string.please_wait_while_send_code))
                    return@setOnClickListener
                }
                val credential = PhoneAuthProvider.getCredential(vrCode!!, code)
                signInWithPhoneAuthCredential(credential)
            }
        }
        save_passwrod.setOnClickListener {
            if (changePasswordValidation()) {
                val code = "${code_1.toText()}${code_2.toText()}${code_3.toText()}${code_4.toText()}${code_5.toText()}${code_6.toText()}"
                VerifyResetCodeApiCall(codePicker.selectedCountryCodeWithPlus + "" + phone.toText(), code, new_password.toText())
            }
        }
        retry.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                sendCodeFromFirebase(codePicker.selectedCountryCodeWithPlus + "" + phone.toText())
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
    }

    fun codeValidation(): Boolean {
        val code = "${code_1.toText()}${code_2.toText()}${code_3.toText()}${code_4.toText()}${code_5.toText()}${code_6.toText()}"

        if (code.length != 6) {
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
                retry_on.text = getString(R.string.retry_on) + " " + helperMethods.MillisUntilToTime(millisUntilFinished)
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

    override fun onBackPressed() {
        if (change_password_layot.visibility == View.VISIBLE) {
            verification_layout.visibility = View.VISIBLE
            change_password_layot.visibility = View.GONE
            helperMethods.setStatusBarColor(this, R.color.orange)
            return
        }
        if (verification_layout.visibility == View.VISIBLE) {
            forgot_password_layout.visibility = View.VISIBLE
            verification_layout.visibility = View.GONE
            helperMethods.setStatusBarColor(this, R.color.white)
            return
        }
        super.onBackPressed()
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
                verification_layout.visibility = View.GONE
                change_password_layot.visibility = View.VISIBLE
                helperMethods.setStatusBarColor(this@ForgotPassword, R.color.white)
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    helperMethods.showToastMessage(getString(R.string.invalid_verification_code))
                } else {
                    helperMethods.showToastMessage(getString(R.string.verification_failed))
                }
            }
        }
    }

    fun forgotPasswordValidation(): Boolean {
        if (phone.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_phone_number))
            return false
        }
//        if (!helperMethods.isValidMobile(codePicker.selectedCountryCodeWithPlus + "" + phone.toText())) {
//            helperMethods.showToastMessage(getString(R.string.enter_vaid_phone_number))
//            return false
//        }

        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun changePasswordValidation(): Boolean {
        if (new_password.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_new_password))
            return false
        }
        if (!helperMethods.isValidPassword(new_password.toText())) {
            helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.password_at_least_8_characters_including_a_lower_case_letteran_uppercase_lettera_number_and_one_special_character))
            return false
        }
        if (confirm_password.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_confirm_password))
            return false
        }
        if (!new_password.toText().equals(confirm_password.toText())) {
            helperMethods.showToastMessage(getString(R.string.new_password_and_confirm_password_not_same))
            return false
        }


        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun forgotPasswordApiCall(phone: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.RESET_PASSWORD_API_CALL(phone,"Consultant")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                sendCodeFromFirebase(phone)

//                                                                val code = jsonObject.getString("code")
//                                val message = jsonObject.getString("message")
//                                                                helperMethods.sendPushNotification("Estisharati", "OTP code is " + code)
//                                helperMethods.showToastMessage(message)
//                                forgot_password_layout.visibility = View.GONE
//                                verification_layout.visibility = View.VISIBLE
//                                ResendCountdown()
//                                helperMethods.setStatusBarColor(this@ForgotPassword, R.color.orange)
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

    fun VerifyResetCodeApiCall(phone: String, code: String, password: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.VERIFY_RESET_CODE_API_CALL(phone, code, password)
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
                                finish()
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

    fun EditText.toText(): String = text.toString().trim()
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
