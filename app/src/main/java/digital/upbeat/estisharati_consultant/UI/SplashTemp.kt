package digital.upbeat.estisharati_consultant.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R

class SplashTemp : AppCompatActivity() {

    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_temp)
        initViews()
        loginProcess()
    }
    fun initViews() {
        helperMethods = HelperMethods(this@SplashTemp)
        preferencesHelper = SharedPreferencesHelper(this@SplashTemp)
    }
        fun loginProcess() {
        GlobalData.BaseUrl = "https://super-servers.com/estisharati/api/v1/${preferencesHelper.appLang}/"
        Log.d("BaseURL", GlobalData.BaseUrl)
        if (preferencesHelper.isConsultantLogIn) {
            startActivity(Intent(this@SplashTemp, ConsultantDrawer::class.java))
        } else {
            startActivity(Intent(this@SplashTemp, Login::class.java))
        }
        finish()
    }

}