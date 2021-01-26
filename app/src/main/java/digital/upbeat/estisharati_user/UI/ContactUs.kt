package digital.upbeat.estisharati_user.UI

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.activity_contact_us.nav_back
import kotlinx.android.synthetic.main.activity_legal_advice.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ContactUs : BaseCompatActivity() {
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
                ContactusApiCall(contactusName.toText(), contactusPhone.toText(), contactusEmail.toText(),GlobalData.homeResponse.message_types.get(messageTypeSpinner.selectedItemPosition).id,contactusSubject.toText(), contactusMsg.toText())
            }
        }
        }

    fun setUserDetails() {
        contactusName.text = (dataUserObject.fname + " " + dataUserObject.lname).toEditable()
        contactusEmail.text = dataUserObject.email.toEditable()
        contactusPhone.text = dataUserObject.phone.toEditable()
        val messageTypesArrayList : ArrayList<String> = arrayListOf()
        for (types in GlobalData.homeResponse.message_types) {
            messageTypesArrayList.add(types.title)
        }
        val adapter = ArrayAdapter(this@ContactUs, R.layout.support_simple_spinner_dropdown_item, messageTypesArrayList)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        messageTypeSpinner.adapter = adapter

    }

    fun contactusValidation(): Boolean {
        if (contactusName.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_name))
            return false
        }
        if (contactusEmail.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_email_address))
            return false
        }
        if (!helperMethods.isvalidEmail(contactusEmail.toText())) {
            helperMethods.showToastMessage(getString(R.string.enter_valid_email_address))
            return false
        }
        if (contactusPhone.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_phone_number))
            return false
        }
        if (!helperMethods.isValidMobile(contactusPhone.toText())) {
            helperMethods.showToastMessage(getString(R.string.enter_valid_phone_number))
            return false
        }

        if (contactusSubject.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_subject))
            return false
        }
        if (contactusMsg.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_message))
            return false
        }
        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun ContactusApiCall(name: String, phone: String, email: String, message_type: String,subject: String, msg: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.CONTACTUS_API_CALL("Bearer ${dataUserObject.access_token}", name, phone, email, message_type,subject,msg)
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
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    finish()
                                    return
                                }
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
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
