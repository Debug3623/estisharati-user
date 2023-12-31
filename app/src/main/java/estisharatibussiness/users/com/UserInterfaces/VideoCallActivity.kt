package estisharatibussiness.users.com.UserInterfaces

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
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
import estisharatibussiness.users.com.UtilsClasses.CustomTouchListener
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.BeautyOptions
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import kotlinx.android.synthetic.main.activity_video_call.*
import kotlinx.android.synthetic.main.activity_video_call.caller_name
import kotlinx.android.synthetic.main.activity_video_call.caller_profile
import kotlinx.android.synthetic.main.activity_video_call.calling_status
import kotlinx.android.synthetic.main.activity_video_call.circle_progress
import kotlinx.android.synthetic.main.activity_video_call.end_call
import kotlinx.android.synthetic.main.activity_video_call.timer
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class VideoCallActivity : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var mRtcEngine: RtcEngine
    var countDownTimer: CountDownTimer? = null
    var showAction: CountDownTimer? = null
    var ringingDuration: CountDownTimer? = null
    var callConnectedTimeMilles = ""
    var enableSpeakerphone = false
    //    var channelUniqueId = ""
    //    var callerId = ""
    //    var callerName = ""
    //    var callerImage = ""
    var player: MediaPlayer? = null
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    lateinit var dataUserFireStore: DataUserFireStore
    lateinit var dataOtherUserFireStore: DataUserFireStore
    lateinit var dataCallsFireStore: DataCallsFireStore
    lateinit var firestoreRegistrar: ListenerRegistration
    var video_balance = 0
    var alertPopupNotShow = true
    val iRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, p1: Int) {
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
                helperMethods.showToastMessage(dataOtherUserFireStore.fname + " " + getString(R.string.join_the_conversation))
                frontAction()
                stopRigntone()
            }
        }

        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onUserMuteAudio(uid: Int, muted: Boolean) {
            runOnUiThread {
                if (muted) {
                    voice_muted_status.text = dataOtherUserFireStore.fname + " " + getString(R.string.muted_this_call)
                    voice_muted_layout.visibility = View.VISIBLE
                } else {
                    voice_muted_layout.visibility = View.GONE
                }
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread { onRemoteUserLeft() }
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            runOnUiThread {
                if (muted) {
                    video_muted_status.text = dataOtherUserFireStore.fname + " " + getString(R.string.video_paused_this_call)
                    video_muted_layout.visibility = View.VISIBLE
                } else {
                    video_muted_layout.visibility = View.GONE
                }
                val container = findViewById<FrameLayout>(R.id.remote_video_view_container)
                val surfaceView = container.getChildAt(0) as SurfaceView
                val tag = surfaceView.tag
                if (tag != null && tag as Int == uid) {
                    surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)


        initViews()
        getDetails()
        clickEvents()
    }

    fun initViews() {
        video_balance = intent.getIntExtra("video_balance", video_balance)
        helperMethods = HelperMethods(this@VideoCallActivity)
        preferencesHelper = SharedPreferencesHelper(this@VideoCallActivity)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        dataUser = preferencesHelper.logInUser
        firestore = FirebaseFirestore.getInstance()
    }

    fun getDetails() { //        if (intent.extras != null) {
        //            channelUniqueId = intent.getStringExtra("channelUniqueId")
        //            callerId = intent.getStringExtra("callerId")
        //            callerName = intent.getStringExtra("callerName")
        //            callerImage = intent.getStringExtra("callerImage")
        //            setDatils()
        //        }
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

    fun setDetails() {
        Glide.with(this@VideoCallActivity).load(dataOtherUserFireStore.image).apply(helperMethods.profileRequestOption).into(caller_profile)
        caller_name.text = dataOtherUserFireStore.fname + " " + dataOtherUserFireStore.lname
        if (ContextCompat.checkSelfPermission(this@VideoCallActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@VideoCallActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initAgoraEngineAndJoinChannel()
        } else {
            helperMethods.selfPermission(this@VideoCallActivity)
        }

        if (dataCallsFireStore.caller_id.equals(dataUserFireStore.user_id)) {
            playRigntone()
            callRingingDuration()
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

    fun clickEvents() {
        video_mute.setOnClickListener {
            val iv = it as ImageView
            if (iv.isSelected) {
                iv.isSelected = false
                iv.clearColorFilter()
            } else {
                iv.isSelected = true
                iv.setColorFilter(ContextCompat.getColor(this@VideoCallActivity, R.color.orange), PorterDuff.Mode.MULTIPLY)
            } // Stops/Resumes sending the local video stream.
            mRtcEngine.muteLocalVideoStream(iv.isSelected)
            val container = findViewById<FrameLayout>(R.id.local_video_view_container)
            val surfaceView = container.getChildAt(0) as SurfaceView
            surfaceView.setZOrderMediaOverlay(!iv.isSelected)
            surfaceView.visibility = if (iv.isSelected) View.GONE else View.VISIBLE
        }
        voice_mute.setOnClickListener {
            val iv = it as ImageView
            if (iv.isSelected) {
                iv.isSelected = false
                iv.clearColorFilter()
            } else {
                iv.isSelected = true
                iv.setColorFilter(ContextCompat.getColor(this@VideoCallActivity, R.color.orange), PorterDuff.Mode.MULTIPLY)
            } // Stops/Resumes sending the local audio stream.
            mRtcEngine.muteLocalAudioStream(iv.isSelected)
        }
        switch_camera.setOnClickListener {
            mRtcEngine.switchCamera()
        }
        end_call.setOnClickListener {
            leaveChannel()
        }

        speaker.setOnClickListener {
            val iv = it as ImageView
            if (enableSpeakerphone) {
                enableSpeakerphone = false
                iv.clearColorFilter()
            } else {
                enableSpeakerphone = true
                iv.setColorFilter(ContextCompat.getColor(this@VideoCallActivity, R.color.orange), PorterDuff.Mode.MULTIPLY)
            }
            mRtcEngine.setEnableSpeakerphone(enableSpeakerphone)
        }

        front_action_layout.setOnClickListener {
            frontAction()
        }

        root.viewTreeObserver.addOnGlobalLayoutListener { local_video_view_container.setOnTouchListener(CustomTouchListener(root.width, root.height)) }
    }

    override fun onDestroy() {
        stopRigntone()
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        if (ringingDuration != null) {
            ringingDuration?.cancel()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
    }

    fun frontAction() {
        if (circle_progress.visibility == View.GONE) {
            header_layout.visibility = View.VISIBLE
            footer_layout.visibility = View.VISIBLE
            if (showAction != null) {
                showAction?.cancel()
            }
            showAction = object : CountDownTimer(5000, 1000) {
                override fun onFinish() {
                    header_layout.visibility = View.GONE
                    footer_layout.visibility = View.GONE
                }

                override fun onTick(millisUntilFinished: Long) {
                }
            }.start()
        }
    }

    private fun initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine()
        setupVideoProfile()
        setupLocalVideo()
        joinChannel()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this@VideoCallActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@VideoCallActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initAgoraEngineAndJoinChannel()
        } else {
            finish()
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
            }
        }.start()
    }

    fun callCountDownTimer() {
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
                    val percentage = video_balance * 0.10
                    val balanceSec = video_balance - diffInSec
                    Log.d("secLeft", "" + percentage + " " + balanceSec)
                    if (0 > balanceSec) {
                        leaveChannel()
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

    fun UpdateConsultationSecondsApiCall(consultant_id: String, video_balance: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.UPDATE_CONSULTATION_SECONDS_API_CALL("Bearer ${dataUser.access_token}", consultant_id, video_balance, "0", "0", "", "", "")
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

    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), iRtcEngineEventHandler)
        } catch (e: Exception) {
            Log.e("Voice_call", Log.getStackTraceString(e))

            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }
    }

    private fun setupVideoProfile() { // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo() //      mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false) // Earlier than 2.3.0
        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15, VideoEncoderConfiguration.STANDARD_BITRATE, VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT))
        mRtcEngine.setBeautyEffectOptions(true, BeautyOptions(BeautyOptions.LIGHTENING_CONTRAST_NORMAL, 0.5f, 0.5f, 0.5f))
    }

    private fun setupLocalVideo() { // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        val container = findViewById<FrameLayout>(R.id.local_video_view_container)
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        container.addView(surfaceView) // Initializes the local video view.
        // RENDER_MODE_FIT: Uniformly scale the video until one of its dimension fits the boundary. Areas that are not filled due to the disparity in the aspect ratio are filled with black.
        mRtcEngine.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun joinChannel() { // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        var token: String? = getString(R.string.agora_access_token)
        if (token!!.isEmpty()) {
            token = null
        }
        mRtcEngine.joinChannel(token, dataCallsFireStore.channel_unique_id, "Extra Optional Data", dataUser.id.toInt()) // if you do not specify the uid, we will generate the uid for you
    }

    private fun setupRemoteVideo(uid: Int) { // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        val container = findViewById<FrameLayout>(R.id.remote_video_view_container)

        if (container.childCount >= 1) {
            return
        }/*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly
          calling SurfaceView.
         */
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        container.addView(surfaceView) // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
        surfaceView.tag = uid // for mark purpose
    }

    private fun leaveChannel() {
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
    }

    private fun onRemoteUserLeft() {
        val container = findViewById<FrameLayout>(R.id.remote_video_view_container)
        container.removeAllViews()
        leaveChannel()
    }
}
