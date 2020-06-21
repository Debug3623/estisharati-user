package digital.upbeat.estisharati_user.UI

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.PinOnKeyListener
import digital.upbeat.estisharati_user.Utils.PinTextWatcher
import kotlinx.android.synthetic.main.activity_forgot_password.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ForgotPassword : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var preferencesHelper: SharedPreferencesHelper
    var resendTimer: CountDownTimer? = null
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
    }

    fun clickEvents() {
        val editTexts = arrayOf(code_1, code_2, code_3, code_4,code_5, code_6, code_7, code_8)

        code_1.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 0, editTexts))
        code_2.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 1, editTexts))
        code_3.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 2, editTexts))
        code_4.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 3, editTexts))
       code_5.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 4, editTexts))
        code_6.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 5, editTexts))
        code_7.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 6, editTexts))
        code_8.addTextChangedListener(PinTextWatcher(this@ForgotPassword, 7, editTexts))

        code_1.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 0, editTexts))
        code_2.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 1, editTexts))
        code_3.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 2, editTexts))
        code_4.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 3, editTexts))
        code_5.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 4, editTexts))
        code_6.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 5, editTexts))
        code_7.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 6, editTexts))
        code_8.setOnKeyListener(PinOnKeyListener(this@ForgotPassword, 7, editTexts))


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
            }
            //            Toast.makeText(this@ForgotPassword,"OTP sent to you email address!", Toast.LENGTH_LONG).show()
            //
            //            val intent = Intent(this@ForgotPassword, Verification::class.java)
            //            intent.putExtra("come_from", "ForgotPassword")
            //            startActivityForResult(intent, 123)
        }
        btn_proceed.setOnClickListener {
            //            Toast.makeText(this@Verification, "OTP verified!", Toast.LENGTH_LONG).show()
            //            val result = Intent()
            //            result.putExtra("value1", "hi")
            //            result.putExtra("value2", "how")
            //            result.putExtra("value3", "are you? $code")
            //            setResult(Activity.RESULT_OK, result);
            //            finish()
            if (codeValidation()) {
                val code = "${code_1.toText()}${code_2.toText()}${code_3.toText()}${code_4.toText()}${code_5.toText()}${code_6.toText()}${code_7.toText()}${code_8.toText()}"
                verification_layout.visibility = View.GONE
                change_password_layot.visibility = View.VISIBLE
                helperMethods.setStatusBarColor(this@ForgotPassword, R.color.white)
            }
        }
        save_passwrod.setOnClickListener {
            if (changePasswordValidation()) {
                val code = "${code_1.toText()}${code_2.toText()}${code_3.toText()}${code_4.toText()}${code_5.toText()}${code_6.toText()}${code_7.toText()}${code_8.toText()}"

                VerifyResetCodeApiCall(codePicker.selectedCountryCodeWithPlus + "" + phone.toText(), code, new_password.toText())
            }
        }
        retry.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                forgotPasswordApiCall(codePicker.selectedCountryCodeWithPlus + "" + phone.toText())
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
    }

    fun codeValidation(): Boolean {
        val code = "${code_1.toText()}${code_2.toText()}${code_3.toText()}${code_4.toText()}${code_5.toText()}${code_6.toText()}${code_7.toText()}${code_8.toText()}"

        if (code.length != 8) {
            helperMethods.showToastMessage("Enter valid code")
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
        resendTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                retry_on.text = "Retry on ${helperMethods.MillisUntilToTime(millisUntilFinished)}"
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

    fun forgotPasswordValidation(): Boolean {
        if (phone.toText().equals("")) {
            helperMethods.showToastMessage("Enter phone number")
            return false
        }
        if (!helperMethods.isValidMobile(codePicker.selectedCountryCodeWithPlus + "" + phone.toText())) {
            helperMethods.showToastMessage("Enter vaid phone number")
            return false
        }

        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun changePasswordValidation(): Boolean {
        if (new_password.toText().equals("")) {
            helperMethods.showToastMessage("Enter new password")
            return false
        }
        if (!helperMethods.isValidPassword(new_password.toText())) {
            helperMethods.AlertPopup("Alert", "Password at least 8 characters including a lower-case letter, an upper–case letter, a number and one special character")
            return false
        }
        if (confirm_password.toText().equals("")) {
            helperMethods.showToastMessage("Enter confirm password")
            return false
        }
        if (!new_password.toText().equals(confirm_password.toText())) {
            helperMethods.showToastMessage("New password and Confirm password not same")
            return false
        }


        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun forgotPasswordApiCall(phone: String) {
        helperMethods.showProgressDialog("Please wait while sending code...")
        val responseBodyCall = retrofitInterface.RESET_PASSWORD_API_CALL(phone)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val code = jsonObject.getString("code")
                                val message = jsonObject.getString("message")
                                helperMethods.sendPushNotification("Estisharati", "OTP code is " + code)
                                helperMethods.showToastMessage(message)
                                forgot_password_layout.visibility = View.GONE
                                verification_layout.visibility = View.VISIBLE
                                ResendCountdown()
                                helperMethods.setStatusBarColor(this@ForgotPassword, R.color.orange)
                            } else {
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)

                                forgot_password_layout.visibility = View.GONE
                                verification_layout.visibility = View.VISIBLE
                                ResendCountdown()
                                helperMethods.setStatusBarColor(this@ForgotPassword, R.color.orange)
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
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun VerifyResetCodeApiCall(phone: String, code: String, password: String) {
        helperMethods.showProgressDialog("Please wait while sending code...")
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
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                val street = data!!.getStringExtra("value1")
                val city = data.getStringExtra("value2")
                val home = data.getStringExtra("value3")
                Log.d("result", "$street $city $home")
                forgot_password_layout.visibility = View.GONE
                change_password_layot.visibility = View.VISIBLE
            }
        }
    }

    fun EditText.toText(): String = text.toString().trim()
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
