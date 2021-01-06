package digital.upbeat.estisharati_consultant.UI

import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var preferencesHelper: SharedPreferencesHelper
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
        nav_back_forgot_password.setOnClickListener { finish() }
        btn_nav_login.setOnClickListener { finish() }
        send_code.setOnClickListener {
            if(forgotPasswordValidation()){
                helperMethods.showToastMessage("sucess")
            }
        }
    }

    fun forgotPasswordValidation(): Boolean {
        if (email_address.toText().equals("")) {
            helperMethods.showToastMessage("Enter email address")
            return false
        }
        if (!helperMethods.isvalidEmail(email_address.toText())) {
            helperMethods.showToastMessage("Enter vaid email address")
            return false
        }

        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun EditText.toText(): String = text.toString().trim()
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
