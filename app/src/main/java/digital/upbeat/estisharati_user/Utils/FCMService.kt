package digital.upbeat.estisharati_user.Utils

import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.UI.SplashScreen

class FCMService : FirebaseMessagingService() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        helperMethods = HelperMethods(this)
        preferencesHelper = SharedPreferencesHelper(this)
        val title = remoteMessage.getData().get("title") as String
        val message = remoteMessage.getData().get("message") as String
        val tag = remoteMessage.getData().get("tag") as String
        helperMethods.sendPushNotification(title, message)

        if (tag.equals("incoming_voice_call") || tag.equals("incoming_video_call")) {
            if (GlobalData.FcmToken.equals("")) {
                if (preferencesHelper.isUserLogIn) {
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
        if (preferencesHelper.isUserLogIn) {
            val hashMap = hashMapOf<String, Any>("fire_base_token" to newToken)
            GlobalData.FcmToken = newToken
            FirebaseFirestore.getInstance().collection("Users").document(preferencesHelper.getLogInUser().id).update(hashMap)
        }
    }
}