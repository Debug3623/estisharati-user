package digital.upbeat.estisharati_user.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R

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
        GlobalData.homeResponseMain = null
        if (preferencesHelper.isUserLogIn) {
            startActivity(Intent(this@SplashTemp, UserDrawer::class.java))
        } else {
            startActivity(Intent(this@SplashTemp, LoginAndRegistration::class.java))
        }
        finish()
    }

}