package estisharatibussiness.users.com.UserInterfaces

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataCallsFireStore
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataUserFireStore
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import kotlinx.android.synthetic.main.activity_voice_call.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class VoiceCallActivity : BaseCompatActivity(), SensorEventListener {
    var player: MediaPlayer? = null
    lateinit var mRtcEngine: RtcEngine
    var enableSpeakerphone = false
    var muteLocalAudioStrea = false
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    lateinit var dataUserFireStore: DataUserFireStore
    lateinit var dataOtherUserFireStore: DataUserFireStore
    lateinit var dataCallsFireStore: DataCallsFireStore
    var countDownTimer: CountDownTimer? = null
    var ringingDuration: CountDownTimer? = null
    var callConnectedTimeMilles = ""
    lateinit var firestoreRegistrar: ListenerRegistration
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    var audio_balance = 0
    var alertPopupNotShow = true
    val iRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserOffline(uid: Int, reason: Int) { // Tutorial Step 4
            runOnUiThread {
                helperMethods.showToastMessage(dataOtherUserFireStore.fname + " " + getString(R.string.left_the_conversation))
                endTheCall()
            }
        }

        override fun onUserMuteAudio(uid: Int, muted: Boolean) {
            runOnUiThread {
                if (muted) {
                    muted_status.text = dataOtherUserFireStore.fname + getString(R.string.muted_this_call)
                    muted_status.visibility = View.VISIBLE
                } else {
                    muted_status.visibility = View.GONE
                }
            } // Tutorial Step 6
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                callConnectedTimeMilles = Date().time.toString()
                if (countDownTimer != null) {
                    countDownTimer?.cancel()
                }
                if (ringingDuration != null) {
                    ringingDuration?.cancel()
                }
                callCountDownTimer()
                calling_status.text = getString(R.string.connected)
                circle_progress.visibility = View.GONE
                stopRigntone()

                helperMethods.showToastMessage(dataOtherUserFireStore.fname + " " + getString(R.string.join_the_conversation))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_call)
        initViews()
        getDetails()
        clickEvents()
    }

    fun initViews() {
        audio_balance = intent.getIntExtra("audio_balance", 0)
        helperMethods = HelperMethods(this@VoiceCallActivity)
        preferencesHelper = SharedPreferencesHelper(this@VoiceCallActivity)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)

        dataUser = preferencesHelper.logInUser
        firestore = FirebaseFirestore.getInstance()




        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    fun playRigntone() {
        player = MediaPlayer.create(this, R.raw.ringtone_fbi)
        player?.isLooping = true // Set looping
        player?.setVolume(100f, 100f)
        player?.start()
    }

    fun stopRigntone() {
        try {
            if (player!!.isPlaying) {
                player?.stop()
                player?.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDetails() {
        firestore.collection("Users").document(dataUser.id).get().addOnSuccessListener {
            dataUserFireStore = it.toObject(DataUserFireStore::class.java)!!
            firestore.collection("Calls").document(dataUserFireStore.channel_unique_id).get().addOnSuccessListener {
                dataCallsFireStore = it.toObject(DataCallsFireStore::class.java)!!
                val contact_person_id = if (dataCallsFireStore.caller_id.equals(dataUserFireStore.user_id)) dataCallsFireStore.receiver_id else dataCallsFireStore.caller_id
                firestore.collection("Users").document(contact_person_id).get().addOnSuccessListener {
                    dataOtherUserFireStore = it.toObject(DataUserFireStore::class.java)!!
                    setDetails()
                }
            }
        }
    }

    fun setDetails() {
        Glide.with(this@VoiceCallActivity).load(dataOtherUserFireStore.image).apply(helperMethods.profileRequestOption).into(caller_profile)
        caller_name.text = dataOtherUserFireStore.fname + " " + dataOtherUserFireStore.lname
        if (ContextCompat.checkSelfPermission(this@VoiceCallActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            initAgoraEngineAndJoinChannel()
        } else {
            helperMethods.selfPermission(this@VoiceCallActivity)
        }

        if (dataCallsFireStore.caller_id.equals(dataUserFireStore.user_id)) {
            callRingingDuration()
            playRigntone()
        } else {
            calling_status.text = getString(R.string.connecting)
        }
        val hashMap = hashMapOf<String, Any>("availability" to true, "channel_unique_id" to "")
        firestoreRegistrar = firestore.collection("Calls").document(dataUserFireStore.channel_unique_id).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                dataCallsFireStore = documentSnapshot.toObject(DataCallsFireStore::class.java)!!
                when (dataCallsFireStore.call_status) {
                    "accept" -> {
                    }
                    "reject" -> {
                        firestoreRegistrar.remove()
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage(getString(R.string.your_call_was_not_accepted))

                        finish()
                    }
                    "cancel" -> {
                        firestoreRegistrar.remove()
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage(getString(R.string.your_call_was_cancelled))

                        finish()
                    }
                    "end_call" -> {
                        firestoreRegistrar.remove()
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage(getString(R.string.your_call_was_ended))

                        finish()
                    }
                    "not_responding" -> {
                        firestoreRegistrar.remove()
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage(getString(R.string.there_is_no_response_your_call))
                        finish()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun callRingingDuration() {
        var hashMap = hashMapOf<String, Any>()
        val ringingCount = dataCallsFireStore.ringing_duration.toLong()
        ringingDuration = object : CountDownTimer(1000 * ringingCount, 1000) {
            override fun onFinish() {
                hashMap = hashMapOf("call_status" to "not_responding")
                helperMethods.updateCallsDetailsToFirestore(dataCallsFireStore.channel_unique_id, hashMap)
            }

            override fun onTick(millisUntilFinished: Long) {
                val time_seconds = millisUntilFinished / 1000
                hashMap = hashMapOf("ringing_duration" to time_seconds.toString())
                helperMethods.updateCallsDetailsToFirestore(dataCallsFireStore.channel_unique_id, hashMap)
                Log.d("ringing_duration", "" + time_seconds)
            }
        }.start()
    }

    fun callCountDownTimer() { //15 mins
        countDownTimer = object : CountDownTimer(1000 * 30 * 30, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val diffInMs: Long = Date().time - callConnectedTimeMilles.toLong()
                val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
                val hours = diffInSec / (60 * 60)
                val tempMint = diffInSec - hours * 60 * 60
                val minutes = tempMint / 60
                val seconds = tempMint - minutes * 60

                Log.d("serviceTimer", "TIME : " + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds))
                timer.text = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
                if (dataCallsFireStore.caller_id.equals(dataUser.id)) {
                    val percentage = audio_balance * 0.10
                    val balanceSec = audio_balance - diffInSec
                    Log.d("secLeft", "" + percentage + " " + balanceSec)
                    if (0 > balanceSec) {
                        endTheCall()
                        helperMethods.showToastMessage(getString(R.string.there_is_no_more_balance_to_continue_this_call))
                    }
                    if (alertPopupNotShow) {
                        if (percentage > balanceSec) {
                            alertPopupNotShow = false
                            helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.there_are_only) + " " + (((balanceSec % 3600) / 60).toString() + "." + (balanceSec % 3600) % 60) + " " + getString(R.string.minutes_left_for_the_conversation_to_end))
                        }
                    }
                }
            }

            override fun onFinish() {
                callCountDownTimer()
            }
        }.start()
    }

    fun endTheCall() {
        var hashMap = hashMapOf<String, Any>()
        if (callConnectedTimeMilles.equals("")) {
            hashMap = hashMapOf("call_status" to "cancel")
            helperMethods.updateCallsDetailsToFirestore(dataCallsFireStore.channel_unique_id, hashMap)
        } else {
            val diffInMs: Long = Date().time - callConnectedTimeMilles.toLong()
            val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
            Log.d("diffInSec", diffInSec.toString())
            if (dataCallsFireStore.caller_id.equals(dataUser.id)) {
                UpdateConsultationSecondsApiCall(dataOtherUserFireStore.user_id, diffInSec.toString())
            }
        }

        mRtcEngine.leaveChannel()
        RtcEngine.destroy()
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
    }

    fun UpdateConsultationSecondsApiCall(consultant_id: String, audio_balance: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.UPDATE_CONSULTATION_SECONDS_API_CALL("Bearer ${dataUser.access_token}", consultant_id, "0", audio_balance, "0", "", "", "")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                var hashMap = hashMapOf<String, Any>()
                hashMap = hashMapOf("call_status" to "end_call")
                helperMethods.updateCallsDetailsToFirestore(dataCallsFireStore.channel_unique_id, hashMap)

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val dataString = jsonObject.getString("data")
                                val dataObject = JSONObject(dataString)
                            } else {
                                val message = jsonObject.getString("message")
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), message)
                            }
                        } catch (e: JSONException) {
                            helperMethods.showToastMessage(getString(R.string.something_went_wrong_on_backend_server))
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("body", "Body Empty")
                    }
                } else {
                    helperMethods.showToastMessage(getString(R.string.something_went_wrong))
                    Log.d("body", "Not Successful")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                var hashMap = hashMapOf<String, Any>()
                hashMap = hashMapOf("call_status" to "end_call")
                helperMethods.updateCallsDetailsToFirestore(dataCallsFireStore.channel_unique_id, hashMap)

                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun clickEvents() {
        end_call.setOnClickListener {
            endTheCall()
        }
        speaker.setOnClickListener {
            val iv = it as ImageView
            if (enableSpeakerphone) {
                enableSpeakerphone = false
                iv.clearColorFilter()
            } else {
                enableSpeakerphone = true
                iv.setColorFilter(ContextCompat.getColor(this@VoiceCallActivity, R.color.orange), PorterDuff.Mode.MULTIPLY)
            }
            mRtcEngine.setEnableSpeakerphone(enableSpeakerphone)
        }
        mute.setOnClickListener {
            val iv = it as ImageView
            if (muteLocalAudioStrea) {
                muteLocalAudioStrea = false
                iv.clearColorFilter()
            } else {
                muteLocalAudioStrea = true
                iv.setColorFilter(ContextCompat.getColor(this@VoiceCallActivity, R.color.orange), PorterDuff.Mode.MULTIPLY)
            }
            mRtcEngine.muteLocalAudioStream(muteLocalAudioStrea)
        }
    }

    private fun initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine() // Tutorial Step 1
        joinChannel() // Tutorial Step 2
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this@VoiceCallActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            initAgoraEngineAndJoinChannel()
        } else {
            helperMethods.showToastMessage(getString(R.string.audio_permission_denied))
            finish()
        }
    }
    // Tutorial Step 1
    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), iRtcEngineEventHandler) // Sets the channel profile of the Agora RtcEngine.
            // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
            // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION) //            mRtcEngine.setEnableSpeakerphone(enableSpeakerphone)
            //            mRtcEngine.muteLocalAudioStream(muteLocalAudioStrea)
        } catch (e: Exception) {
            Log.e("voice", Log.getStackTraceString(e))
        }
    }
    // Tutorial Step 2
    private fun joinChannel() {
        var accessToken: String? = getString(R.string.agora_access_token)
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
            accessToken = null // default, no token
        } // Allows a user to join a channel.
        mRtcEngine.joinChannel(accessToken, dataCallsFireStore.channel_unique_id, "Extra Optional Data", dataUser.id.toInt()) // if you do not specify the uid, we will generate the uid for you
    }

    override fun onBackPressed() {
    }

    override fun onResume() { // Register a listener for the sensor.
        super.onResume()

        sensor?.also { proximity ->
            sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() { // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRigntone()
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        if (ringingDuration != null) {
            ringingDuration?.cancel()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("SensorEvent", "onAccuracyChanged")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val distance = event!!.values[0]
        val params: WindowManager.LayoutParams = this.window.attributes
        if (distance == 0f) {
            params.screenBrightness = 0f
            this.window.attributes = params
            enableDisableViewGroup(activity_voice_chat_view.parent as ViewGroup, false)
            Log.e("onSensorChanged", "NEAR")
        } else {
            params.screenBrightness = -1.0f
            this.window.attributes = params
            enableDisableViewGroup(activity_voice_chat_view.parent as ViewGroup, true)
            Log.e("onSensorChanged", "FAR")
        }
    }

    fun enableDisableViewGroup(viewGroup: ViewGroup, enabled: Boolean) {
        val childCount: Int = viewGroup.childCount
        for (i in 0 until childCount) {
            val view: View = viewGroup.getChildAt(i)
            view.isEnabled = enabled
            if (view is ViewGroup) {
                enableDisableViewGroup(view, enabled)
            }
        }
    }
}