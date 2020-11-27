package digital.upbeat.estisharati_user.UI

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import digital.upbeat.estisharati_user.CommonApiHelper.SendDeviceTokenHelper
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import digital.upbeat.estisharati_user.Utils.alertActionClickListner

class SplashScreen : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
            if (!it.isSuccessful) {
                Log.d("FirebaseInstanceId   ", "FirebaseInstanceId failed", it.exception)
                return@OnCompleteListener
            }
            it.result?.let {
                GlobalData.FcmToken = it.token
            }
            Log.e("FirebaseInstanceId", it.result!!.token)
        })

        initViews()
        startCountDownTimer()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@SplashScreen)
        preferencesHelper = SharedPreferencesHelper(this@SplashScreen)
        helperMethods.setStatusBarColor(this, R.color.white)
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
            if (preferencesHelper.isUserLogIn) {
                SendDeviceTokenHelper(this@SplashScreen, this@SplashScreen, true).SendDeviceTokenFirebase()
            } else {
                checkSelfPermission()
            }
        } else {
            helperMethods.showAlertDialog(this@SplashScreen, object : alertActionClickListner {
                override fun onActionOk() {
                    checkInternetConnection(getString(R.string.please_check_your_internet_connection_and_try_again))
                }

                override fun onActionCancel() {
                    finish()
                }
            }, "No Internet Connection", errorMsg, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))

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
        if (preferencesHelper.isUserLogIn) {
            startActivity(Intent(this@SplashScreen, UserDrawer::class.java))
        } else {
            startActivity(Intent(this@SplashScreen, LoginAndRegistration::class.java))
        }
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        loginProcess()
    }
}
