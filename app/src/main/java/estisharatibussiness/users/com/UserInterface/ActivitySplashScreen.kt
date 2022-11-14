package estisharatibussiness.users.com.UserInterface

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.Utils.BaseCompatActivity
import estisharatibussiness.users.com.Utils.alertActionClickListner

class ActivitySplashScreen : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
//    var appUpdateManager: AppUpdateManager? = null
//    var UPDATE_REQUEST = 1234
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
//        checkUpdateAvailable()
        launchPage()
    }

//    private fun checkUpdateAvailable() {
//        appUpdateManager = AppUpdateManagerFactory.create(this@SplashScreen)
//        appUpdateManager!!.appUpdateInfo.addOnSuccessListener(OnSuccessListener { appUpdateInfo: AppUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                try {
//                    appUpdateManager!!.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this@SplashScreen, UPDATE_REQUEST)
//                } catch (e: SendIntentException) {
//                    e.printStackTrace()
//                    launchPage()
//                    Log.d("inAppUpdate", "SendIntentException")
//                }
//            } else {
//                launchPage()
//                Log.d("inAppUpdate", "No update available")
//            }
//        }).addOnFailureListener(OnFailureListener { e ->
//            e.printStackTrace()
//            launchPage()
//            Log.d("inAppUpdate", "addOnFailureListener")
//        })
//    }
//
//    override fun onResume() {
//        super.onResume()
//        appUpdateManager!!.appUpdateInfo.addOnSuccessListener(OnSuccessListener { appUpdateInfo: AppUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                try {
//                    appUpdateManager!!.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this@SplashScreen, UPDATE_REQUEST)
//                } catch (e: SendIntentException) {
//                    e.printStackTrace()
//                    Log.d("inAppUpdate", "SendIntentException")
//                }
//            } else {
//                Log.d("inAppUpdate", "No update available")
//            }
//        }).addOnFailureListener(OnFailureListener { e ->
//            e.printStackTrace()
//            Log.d("inAppUpdate", "addOnFailureListener")
//        })
//    }

    private fun launchPage() {
        initViews()
        handleDynamicLink()
        startCountDownTimer()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == UPDATE_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(this@SplashScreen, getString(R.string.app_update_start), Toast.LENGTH_LONG).show()
//                Log.d("inAppUpdate", getString(R.string.app_update_start))
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this@SplashScreen, getString(R.string.app_update_canceled), Toast.LENGTH_LONG).show()
//                Log.d("inAppUpdate", getString(R.string.app_update_canceled))
//                finish()
//            } else if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
//                Toast.makeText(this@SplashScreen, getString(R.string.app_update_failed), Toast.LENGTH_LONG).show()
//                Log.d("inAppUpdate", getString(R.string.app_update_failed))
//                finish()
//            }
//        }
//    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivitySplashScreen)
        preferencesHelper = SharedPreferencesHelper(this@ActivitySplashScreen)
        helperMethods.setStatusBarColor(this, R.color.white)
        notFromNotification = intent.getBooleanExtra("notFromNotification", true)
        if (preferencesHelper.isUserLogIn && notFromNotification) {
            val hashMap = hashMapOf<String, Any>("availability" to true, "channel_unique_id" to "")
            helperMethods.updateUserDetailsToFirestore(preferencesHelper.logInUser.id, hashMap)
        }
    }

    fun handleDynamicLink() {
        Firebase.dynamicLinks.getDynamicLink(intent).addOnSuccessListener(this) { pendingDynamicLinkData -> // Get deep link from result (may be null if no link is found)
            if (pendingDynamicLinkData != null) {
                pendingDynamicLinkData.link?.let {
                    pendingDynamicLinkData.link?.getQueryParameter("referral_code")?.let {
                        GlobalData.referralCode = it
                        Log.d("dynamicLinks", it)
                    }
                    pendingDynamicLinkData.link?.getQueryParameter("courseId")?.let {
                        GlobalData.courseId = it
                        Log.d("dynamicLinks", it)
                    }
                    pendingDynamicLinkData.link?.getQueryParameter("surveyId")?.let {
                        GlobalData.surveyId = it
                        Log.d("dynamicLinks", it)
                    }
                }
            }
        }.addOnFailureListener(this) { e -> Log.e("dynamicLinks", "getDynamicLink:onFailure", e) }
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
        if (helperMethods.isConnectingToInternet) { //            if (preferencesHelper.isUserLogIn) {
            //                SendDeviceTokenHelper(this@SplashScreen, this@SplashScreen, true).SendDeviceTokenFirebase()
            //            } else {
            checkSelfPermission() //            }
        } else {
            helperMethods.showAlertDialog(this@ActivitySplashScreen, object : alertActionClickListner {
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
        if (ContextCompat.checkSelfPermission(this@ActivitySplashScreen, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this@ActivitySplashScreen, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this@ActivitySplashScreen, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this@ActivitySplashScreen, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            helperMethods.selfPermission(this@ActivitySplashScreen)
        } else {
            loginProcess()
        }
    }

    fun loginProcess() {
        GlobalData.BaseUrl = "https://super-servers.com/estisharati/api/v1/${preferencesHelper.appLang}/"
        Log.d("BaseURL", GlobalData.BaseUrl)
        GlobalData.homeResponseMain = null
        if (preferencesHelper.isUserLogIn) {
            startActivity(Intent(this@ActivitySplashScreen, UserDrawerActivity::class.java))
        } else {
            startActivity(Intent(this@ActivitySplashScreen, LoginAndRegistration::class.java))
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
