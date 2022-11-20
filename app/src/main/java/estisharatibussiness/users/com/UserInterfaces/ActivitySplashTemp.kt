package estisharatibussiness.users.com.UserInterfaces

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R

class ActivitySplashTemp : AppCompatActivity() {

    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_temp)
        initViews()
        loginProcess()
    }
    fun initViews() {
        helperMethods = HelperMethods(this@ActivitySplashTemp)
        preferencesHelper = SharedPreferencesHelper(this@ActivitySplashTemp)
    }
        fun loginProcess() {
        GlobalData.BaseUrl = "https://super-servers.com/estisharati/api/v1/${preferencesHelper.appLang}/"
        Log.d("BaseURL", GlobalData.BaseUrl)
        GlobalData.homeResponseMain = null
        if (preferencesHelper.isUserLogIn) {
            startActivity(Intent(this@ActivitySplashTemp, UserDrawerActivity::class.java))
        } else {
            startActivity(Intent(this@ActivitySplashTemp, LoginAndRegistration::class.java))
        }
        finish()
    }

}