package digital.upbeat.estisharati_consultant.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.DataClassHelper.DataSubscription
import digital.upbeat.estisharati_consultant.DataClassHelper.DataUser
import digital.upbeat.estisharati_consultant.DataClassHelper.DataUserMetas
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Login : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    var eyeVisible: Boolean = false
    lateinit var retrofitInterface: RetrofitInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Login)
        preferencesHelper = SharedPreferencesHelper(this@Login)
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
        nav_forget_password.setOnClickListener {
//            startActivity(Intent(this@Login, ForgotPassword::class.java))
        }
        terms_policy.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                val intent = Intent(this@Login, Pages::class.java)
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
    }

    fun loginValidation(): Boolean {
        if (email_phone_number.toText().equals("")) {
            helperMethods.showToastMessage("Enter email address")
            return false
        }
        if (!helperMethods.isvalidEmail(email_phone_number.toText())) {
            helperMethods.AlertPopup("Alert", "Enter valid email address")
            return false
        }
        if (password.toText().equals("")) {
            helperMethods.showToastMessage("Enter password")
            return false
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

    fun logInApiCall(userIdStr: String, password: String, remember: String) {
        helperMethods.showProgressDialog("Please wait while login in...")
        val responseBodyCall = retrofitInterface.LOGIN_API_CALL(userIdStr, password, remember, GlobalData.FcmToken,"Consultant")
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
                                //                                val userObject = JSONObject(userString)
                                //                                val id = userObject.getString("id")
                                //                                val fname = userObject.getString("fname")
                                //                                val lname = userObject.getString("lname")
                                //                                val email = userObject.getString("email")
                                //                                val phone = userObject.getString("phone")
                                //                                val image = userObject.getString("image")
                                //                                val member_since = userObject.getString("member_since")
                                //                                val access_token = userObject.getString("access_token")
                                //                                val expired_days = userObject.getString("expired_days")
                                //                                val user_metasStr = userObject.getString("user_metas")
                                //                                val userMetasObject = JSONObject(user_metasStr)
                                //                                val job_title = userMetasObject.getString("job_title")
                                //                                val city = userMetasObject.getString("city")
                                //                                val phone_code = if (userMetasObject.getString("phone_code").equals("")) "+20" else userMetasObject.getString("phone_code")
                                //                                val consultant_cost = userMetasObject.getString("consultant_cost")
                                //                                //                                val qualification = userMetasObject.getString("qualification")
                                //                                val qualification = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
                                //                                val qualification_brief = userMetasObject.getString("qualification_brief")
                                //                                val fire_base_token = userMetasObject.getString("fire_base_token")
                                //                                val country = userMetasObject.getString("country")
                                //                                val user_metas = DataUserMetas(job_title, city, phone_code, consultant_cost, qualification, qualification_brief, fire_base_token, country)
                                //                                val subscription_str = userObject.getString("subscription")
                                //                                val subscriptionObject = JSONObject(subscription_str)
                                //                                val courses = subscriptionObject.getString("courses")
                                //                                val consultations = subscriptionObject.getString("consultations")
                                //                                val current_package = subscriptionObject.getString("package")
                                //                                val subscription = DataSubscription(courses, consultations, current_package)
                                val dataUser = Gson().fromJson(userString, DataUser::class.java)
                                val hashMap = hashMapOf<String, Any>()
                                hashMap.put("user_id", dataUser.id)
                                hashMap.put("fname", dataUser.fname)
                                hashMap.put("lname", dataUser.lname)
                                hashMap.put("email", dataUser.email)
                                hashMap.put("phone", dataUser.phone)
                                hashMap.put("image", dataUser.image)
                                hashMap.put("fire_base_token", dataUser.user_metas.fire_base_token)
                                hashMap.put("user_type", "consultant")
                                hashMap.put("online_status", true)
                                hashMap.put("last_seen", FieldValue.serverTimestamp())
                                hashMap.put("availability", true)
                                hashMap.put("channel_unique_id", "")
                                helperMethods.setUserDetailsToFirestore(dataUser.id, hashMap)
                                preferencesHelper.isConsultantLogIn = true
                                preferencesHelper.logInConsultant = dataUser
                                startActivity(Intent(this@Login, ConsultantDrawer::class.java))
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

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}
