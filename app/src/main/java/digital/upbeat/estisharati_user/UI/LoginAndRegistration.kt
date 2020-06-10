package digital.upbeat.estisharati_user.UI

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import com.hexagonitsolutions.ridehomeuser.ApiHelper.RetrofitApiClient
import com.hexagonitsolutions.ridehomeuser.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.DataUserMetas
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_login_and_registration.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginAndRegistration : AppCompatActivity() {
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
        register.setOnClickListener {
            val intent = Intent(this@LoginAndRegistration, Verification::class.java)
            intent.putExtra("come_from", "LoginAndRegistration")
            startActivityForResult(intent, 123)
        }
        nav_forget_password.setOnClickListener { startActivity(Intent(this@LoginAndRegistration, ForgotPassword::class.java)) }
        terms_policy.setOnClickListener {
            val intent = Intent(this@LoginAndRegistration, WebView::class.java)
            intent.putExtra("action_bar_title", "Terms and policy")
            intent.putExtra("url", "https://upbeat.digital/en/")
            startActivity(intent)
        }
        terms_policy1.setOnClickListener {
            val intent = Intent(this@LoginAndRegistration, WebView::class.java)
            intent.putExtra("action_bar_title", "Terms and policy")
            intent.putExtra("url", "https://upbeat.digital/en/")
            startActivity(intent)
        }
        btn_login.setOnClickListener {
            if (loginValidation()) {
                LgoinInApiCall(email_phone_number.toText(), password.toText())
            }
        }
    }

    fun loginValidation(): Boolean {
        if (email_phone_number.toText().equals("")) {
            helperMethods.showToastMessage("Enter email address or phone number")
            return false
        }
        if (password.toText().equals("")) {
            helperMethods.showToastMessage("Enter password")
            return false
        }
        if (!helperMethods.isvalidEmail(email_phone_number.toText())) {
            if (!helperMethods.isValidMobile(email_phone_number.toText())) {
                helperMethods.AlertPopup("Alert", "Enter valid email address or vaid phone number with country code")
                return false
            }
        }
        if (!helperMethods.isValidPassword(password.toText())) {
            helperMethods.AlertPopup("Alert", "Password at least 8 characters including a lower-case letter, an upper–case letter, a number and one special character")
            return false
        }

        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun LgoinInApiCall(userId: String, password: String) {
        helperMethods.showProgressDialog("Please wait while login in...")
        val responseBodyCall = retrofitInterface.LOGIN_API_CALL(userId, password)
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
                                val userObject = JSONObject(userString)
                                val id = userObject.getString("id")
                                val fname = userObject.getString("fname")
                                val lname = userObject.getString("lname")
                                val email = userObject.getString("email")
                                val phone = userObject.getString("phone")
                                val image = userObject.getString("image")
                                val member_since = userObject.getString("member_since")
                                val user_metasStr = userObject.getString("user_metas")
                                val userMetasObject = JSONObject(user_metasStr)
                                val city = userMetasObject.getString("city")
                                val contact = userMetasObject.getString("contact")
                                val user_metas = DataUserMetas(city, contact)
                                val access_token = userObject.getString("access_token")
                                val dataUser = DataUser(id, fname, lname, email, phone, image, member_since, user_metas, access_token)
                                preferencesHelper.isUserLogIn = true
                                preferencesHelper.setLogInUser(dataUser)
                                startActivity(Intent(this@LoginAndRegistration, UserDrawer::class.java))
                                finish()
                            } else {
                                val message = jsonObject.getString("message")
                                helperMethods.AlertPopup("Alert", message)
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
                startActivity(Intent(this@LoginAndRegistration, OnBoarding::class.java))
                finish()
            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}
