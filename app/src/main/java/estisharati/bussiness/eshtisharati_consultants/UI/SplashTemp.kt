package estisharati.bussiness.eshtisharati_consultants.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import estisharati.bussiness.eshtisharati_consultants.Helper.GlobalData
import estisharati.bussiness.eshtisharati_consultants.Helper.HelperMethods
import estisharati.bussiness.eshtisharati_consultants.Helper.SharedPreferencesHelper
import estisharati.bussiness.eshtisharati_consultants.R

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
        GlobalData.BaseUrl = "https://apptocom.com/estisharati/api/v1/${preferencesHelper.appLang}/"
        Log.d("BaseURL", GlobalData.BaseUrl)
        if (preferencesHelper.isConsultantLogIn) {
            startActivity(Intent(this@SplashTemp, ConsultantDrawer::class.java))
        } else {
            startActivity(Intent(this@SplashTemp, Login::class.java))
        }
        finish()
    }

}