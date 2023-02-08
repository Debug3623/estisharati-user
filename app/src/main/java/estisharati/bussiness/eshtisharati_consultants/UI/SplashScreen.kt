package estisharati.bussiness.eshtisharati_consultants.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import estisharati.bussiness.eshtisharati_consultants.Helper.GlobalData
import estisharati.bussiness.eshtisharati_consultants.Helper.HelperMethods
import estisharati.bussiness.eshtisharati_consultants.Helper.SharedPreferencesHelper
import estisharati.bussiness.eshtisharati_consultants.R
import estisharati.bussiness.eshtisharati_consultants.Utils.BaseCompatActivity
import estisharati.bussiness.eshtisharati_consultants.Utils.alertActionClickListner

class SplashScreen : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    var notFromNotification = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            try {
                if (it.isComplete) {
                    GlobalData.FcmToken = it.result.toString()
                    Log.e("FirebaseInstanceId", GlobalData.FcmToken)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        initViews()
        startCountDownTimer()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@SplashScreen)
        preferencesHelper = SharedPreferencesHelper(this@SplashScreen)
        notFromNotification = intent.getBooleanExtra("notFromNotification", true)
        helperMethods.setStatusBarColor(this, R.color.white)
        if (preferencesHelper.isConsultantLogIn && notFromNotification) {
            val hashMap = hashMapOf<String, Any>("availability" to true, "channel_unique_id" to "")
            helperMethods.updateUserDetailsToFirestore(
                preferencesHelper.logInConsultant.id,
                hashMap
            )
        }
    }

    fun startCountDownTimer() {
        object : CountDownTimer(2000, 1000) {
            override fun onFinish() {
                checkInternetConnection(getString(R.string.please_check_your_internet_connection_and_try_again))
            }

            override fun onTick(millisUntilFinished: Long) {
            }
        }.start()
    }

    fun checkInternetConnection(errorMsg: String) {
        if (helperMethods.isConnectingToInternet) {
            checkSelfPermission()
        } else {
            helperMethods.showAlertDialog(this@SplashScreen, object : alertActionClickListner {
                override fun onActionOk() {
                    checkInternetConnection(getString(R.string.please_check_your_internet_connection_and_try_again))
                }

                override fun onActionCancel() {
                    finish()
                }
            }, getString(R.string.no_internet_connection), errorMsg, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))
        }
    }

    fun checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this@SplashScreen, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this@SplashScreen, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this@SplashScreen, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this@SplashScreen, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            helperMethods.selfPermission(this@SplashScreen)
        } else {
            loginProcess()
        }
    }

    fun loginProcess() {
        GlobalData.BaseUrl = "https://apptocom.com/estisharati/api/v1/${preferencesHelper.appLang}/"
        Log.d("BaseURL", GlobalData.BaseUrl+"     "+preferencesHelper.logInConsultant.access_token)
        if (preferencesHelper.isConsultantLogIn) {
            Log.d("BaseURL", preferencesHelper.logInConsultant.access_token)
            startActivity(Intent(this@SplashScreen, ConsultantDrawer::class.java))
        } else {
            startActivity(Intent(this@SplashScreen, Login::class.java))
        }
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        loginProcess()
    }
}
