package digital.upbeat.estisharati_consultant.Utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import digital.upbeat.estisharati_consultant.CommonApiHelper.SendDeviceTokenHelper
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper

class FCMService : FirebaseMessagingService() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        helperMethods = HelperMethods(this)
        preferencesHelper = SharedPreferencesHelper(this)
        var title = ""
        var body = ""
        var image_url = ""
        var tag = ""
        remoteMessage.notification?.let {
            it.title?.let { title_it -> title = title_it }
            it.body?.let { body_it -> body = body_it }
            it.imageUrl?.let { imageUrl_it -> image_url = imageUrl_it.toString() }
            it.tag?.let { tag_it -> tag = tag_it }
        }

        if (tag != "incoming_message") {
            helperMethods.sendPushNotification(title, body, tag, image_url)
        }
        //            if (tag == "incoming_voice_call" || tag == "incoming_video_call") {
        //                if (GlobalData.FcmToken.equals("")) {
        //                    if (preferencesHelper.isUserLogIn) {
        //                        val intent = Intent(this, SplashScreen::class.java)
        //                        intent.putExtra("notFromNotification", false)
        //                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        //                        this.startActivity(intent)
        //                    }
        //                }
        //            }
        Log.d("FCM_message", title + "    " + body + "    " + tag + "    " + image_url)
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