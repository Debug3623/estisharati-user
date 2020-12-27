package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_contact_us.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ContactUs : AppCompatActivity() {
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var helperMethods: HelperMethods
    lateinit var dataUserObject: DataUser
    lateinit var retrofitInterface: RetrofitInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        initViews()
        clickEvents()
        setUserDetails()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ContactUs)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ContactUs)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUserObject = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }

        contactusSend.setOnClickListener {
            if (contactusValidation()) {
                ContactusApiCall(contactusName.toText(), contactusPhone.toText(), contactusEmail.toText(), contactusMsg.toText())
            }
        }
    }

    fun setUserDetails() {
    contactusName.text=(dataUserObject.fname+" "+dataUserObject.lname).toEditable()
    contactusEmail.text=dataUserObject.email.toEditable()
    contactusPhone.text=dataUserObject.phone.toEditable()

    }

    fun contactusValidation(): Boolean {
        if (contactusName.toText().equals("")) {
            helperMethods.showToastMessage("Enter your name")
            return false
        }
        if (contactusEmail.toText().equals("")) {
            helperMethods.showToastMessage("Enter email address")
            return false
        }
        if (!helperMethods.isvalidEmail(contactusEmail.toText())) {
            helperMethods.showToastMessage("Enter valid email address")
            return false
        }
        if (contactusPhone.toText().equals("")) {
            helperMethods.showToastMessage("Enter your phone number")
            return false
        }
        if (!helperMethods.isValidMobile(contactusPhone.toText())) {
            helperMethods.showToastMessage("Enter valid phone number")
            return false
        }

        if (contactusMsg.toText().equals("")) {
            helperMethods.showToastMessage("Enter your message")
            return false
        }
        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun ContactusApiCall(name: String, phone: String, email: String, msg: String) {
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.CONTACTUS_API_CALL("Bearer ${dataUserObject.access_token}", name, phone, email, msg)
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

    fun EditText.toText(): String = text.toString().trim()
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

}
