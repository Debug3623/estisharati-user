package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_login_and_registration.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginAndRegistration : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    var eyeVisible: Boolean = false
    var eyeVisibleReg: Boolean = false
    lateinit var retrofitInterface: RetrofitInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_registration)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@LoginAndRegistration)
        preferencesHelper = SharedPreferencesHelper(this@LoginAndRegistration)
        helperMethods.setStatusBarColor(this, R.color.white)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
    }

    fun clickEvents() {
        if (eyeVisible) {
            eye.setBackgroundResource(R.drawable.ic_eye_invisible)
            password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            eye.setBackgroundResource(R.drawable.ic_eye_visible)
            password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        eye.setOnClickListener {
            if (eyeVisible) {
                eyeVisible = false
                eye.setBackgroundResource(R.drawable.ic_eye_visible)
                password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                eyeVisible = true
                eye.setBackgroundResource(R.drawable.ic_eye_invisible)
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }
        if (eyeVisibleReg) {
            reg_eye.setBackgroundResource(R.drawable.ic_eye_invisible)
            reg_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            reg_eye.setBackgroundResource(R.drawable.ic_eye_visible)
            reg_password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        reg_eye.setOnClickListener {
            if (eyeVisibleReg) {
                eyeVisibleReg = false
                reg_eye.setBackgroundResource(R.drawable.ic_eye_visible)
                reg_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                eyeVisibleReg = true
                reg_eye.setBackgroundResource(R.drawable.ic_eye_invisible)
                reg_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        nav_login.setOnClickListener {
            login_layout.visibility = View.VISIBLE
            register_layout.visibility = View.GONE
        }
        btn_nav_login.setOnClickListener {
            login_layout.visibility = View.VISIBLE
            register_layout.visibility = View.GONE
        }

        nav_register.setOnClickListener {
            login_layout.visibility = View.GONE
            register_layout.visibility = View.VISIBLE
        }
        btn_nav_register.setOnClickListener {
            login_layout.visibility = View.GONE
            register_layout.visibility = View.VISIBLE
        }

        nav_forget_password.setOnClickListener { startActivity(Intent(this@LoginAndRegistration, ForgotPassword::class.java)) }
        terms_policy.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                val intent = Intent(this@LoginAndRegistration, Pages::class.java)
                intent.putExtra("page", "terms-and-conditions")
                startActivity(intent)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        terms_policy1.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                val intent = Intent(this@LoginAndRegistration, Pages::class.java)
                intent.putExtra("page", "terms-and-conditions")
                startActivity(intent)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        btn_login.setOnClickListener {
            val remember = if (remember_me.isChecked) "on" else "off"
            if (loginValidation()) {
                logInApiCall(email_phone_number.toText(), password.toText(), remember)
            }
        }
        register.setOnClickListener {
            if (registrationValidation()) {
                registrationApiCall(reg_fname.toText(), reg_lname.toText(), reg_email_address.toText(), codePicker.selectedCountryCodeWithPlus + "" + reg_phone.toText(), codePicker.selectedCountryCodeWithPlus, reg_password.toText())
            }
        }
    }

    fun loginValidation(): Boolean {
        if (email_phone_number.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_email_address))
            return false
        }
        if (!helperMethods.isvalidEmail(email_phone_number.toText())) {
            helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.enter_valid_email_address))
            return false
        }
        if (password.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_password))
            return false
        }
        if (!helperMethods.isValidPassword(password.toText())) {
            helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.password_at_least_8_characters_including_a_lower_case_letteran_uppercase_lettera_number_and_one_special_character))
            return false
        }

        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun logInApiCall(userIdStr: String, password: String, remember: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.LOGIN_API_CALL(userIdStr, password, remember, GlobalData.FcmToken,"User")
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
                                helperMethods.setUserDetailsToFirestore(dataUser.id, hashMap)
                                preferencesHelper.isUserLogIn = true
                                preferencesHelper.logInUser = dataUser
                                startActivity(Intent(this@LoginAndRegistration, UserDrawer::class.java))
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

    fun registrationApiCall(fname: String, lname: String, email: String, phone: String, phone_code: String, passwrod: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.REGISTER_API_CALL(fname, lname, email, phone, phone_code, passwrod, "User")
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
                                val intent = Intent(this@LoginAndRegistration, Verification::class.java)
                                intent.putExtra("come_from", "Registration")
                                intent.putExtra("phone", phone)
                                intent.putExtra("email", email)
                                intent.putExtra("password", passwrod)
                                intent.putExtra("verified", "")
                                startActivity(intent)
                            } else {
                                val message = jsonObject.getString("message")
                                val verified = jsonObject.optBoolean("verified", true)
                                if (!verified) {
                                    val intent = Intent(this@LoginAndRegistration, Verification::class.java)
                                    intent.putExtra("come_from", "Registration")
                                    intent.putExtra("phone", phone)
                                    intent.putExtra("email", email)
                                    intent.putExtra("password", passwrod)
                                    intent.putExtra("verified", verified.toString())

                                    startActivity(intent)
                                }
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

    fun registrationValidation(): Boolean {
        if (reg_fname.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_first_name))
            return false
        }
        if (reg_lname.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_last_name))
            return false
        }
        if (reg_email_address.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_email_address))
            return false
        }
        if (!helperMethods.isvalidEmail(reg_email_address.toText())) {
            helperMethods.showToastMessage(getString(R.string.enter_valid_email_address))
            return false
        }
        if (reg_phone.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_phone_number))
            return false
        }
        if (!helperMethods.isValidMobile(codePicker.selectedCountryCodeWithPlus + "" + reg_phone.toText())) {
            helperMethods.showToastMessage(getString(R.string.enter_vaid_phone_number))
            return false
        }
        if (reg_password.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_password))
            return false
        }
        if (!helperMethods.isValidPassword(reg_password.toText())) {
            helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.password_at_least_8_characters_including_a_lower_case_letteran_uppercase_lettera_number_and_one_special_character))
            return false
        }
        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
         }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}
