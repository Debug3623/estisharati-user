package digital.upbeat.estisharati_consultant.UI

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
import com.google.firebase.messaging.FirebaseMessaging
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.CommonApiHelper.SendDeviceTokenHelper
import digital.upbeat.estisharati_consultant.Helper.GlobalData

class SplashScreen : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
                GlobalData.FcmToken = it.result.toString()
                Log.e("FirebaseInstanceId", GlobalData.FcmToken)
            }
        }
        initViews()
        startCountDownTimer()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@SplashScreen)
        preferencesHelper = SharedPreferencesHelper(this@SplashScreen)
        helperMethods.setStatusBarColor(this, R.color.white)
        if (preferencesHelper.isConsultantLogIn) {
            val hashMap = hashMapOf<String, Any>("availability" to true, "channel_unique_id" to "")
            helperMethods.updateUserDetailsToFirestore(preferencesHelper.logInConsultant.id, hashMap)
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
            if (preferencesHelper.isConsultantLogIn) {
                SendDeviceTokenHelper(this@SplashScreen, this@SplashScreen, true).SendDeviceTokenFirebase()
            } else {
                checkSelfPermission()
            }
        } else {
            val popup_view = LayoutInflater.from(this@SplashScreen).inflate(R.layout.confirmation_alert_popup, null)
            val aleatdialog = AlertDialog.Builder(this@SplashScreen)
            aleatdialog.setView(popup_view)
            aleatdialog.setCancelable(false)
            val dialog = aleatdialog.create()
            dialog.show()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val title = popup_view.findViewById<TextView>(R.id.title)
            val message = popup_view.findViewById<TextView>(R.id.message)
            title.text = "No Internet Connection"
            val action_cancel = popup_view.findViewById<TextView>(R.id.action_cancel)
            val action_ok = popup_view.findViewById<TextView>(R.id.action_ok)
            message.text = errorMsg
            action_cancel.setOnClickListener {
                dialog.dismiss()
                finish()
            }

            action_ok.setOnClickListener {
                dialog.dismiss()
                checkInternetConnection(getString(R.string.please_check_your_internet_connection_and_try_again))
            }
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
        if (preferencesHelper.isConsultantLogIn) {
            startActivity(Intent(this@SplashScreen,ConsultantDrawer::class.java))
        } else {
            startActivity(Intent(this@SplashScreen,Login::class.java))
        }
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        loginProcess()
    }
}
