package digital.upbeat.estisharati_consultant.Utils

import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.UI.SplashScreen
import digital.upbeat.estisharati_consultant.CommonApiHelper.SendDeviceTokenHelper
import digital.upbeat.estisharati_consultant.Helper.GlobalData

class FCMService : FirebaseMessagingService() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        helperMethods = HelperMethods(this)
        preferencesHelper = SharedPreferencesHelper(this)
        val title = remoteMessage.getData().get("title") as String
        val body = remoteMessage.getData().get("body") as String
        val type = remoteMessage.getData().get("type") as String
        helperMethods.sendPushNotification(title, body)

        if (type.equals("incoming_voice_call") || type.equals("incoming_video_call")) {
            if (GlobalData.FcmToken.equals("")) {
                if (preferencesHelper.isConsultantLogIn) {
                    val intent = Intent(this, SplashScreen::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    this.startActivity(intent)
                }
            }
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        helperMethods = HelperMethods(this)
        preferencesHelper = SharedPreferencesHelper(this)
        if (preferencesHelper.isConsultantLogIn) {
            val hashMap = hashMapOf<String, Any>("fire_base_token" to newToken)
            GlobalData.FcmToken = newToken
            FirebaseFirestore.getInstance().collection("Users").document(preferencesHelper.logInConsultant.id).update(hashMap)
            SendDeviceTokenHelper(this, null, false).SendDeviceTokenFirebase()

        }
    }
}