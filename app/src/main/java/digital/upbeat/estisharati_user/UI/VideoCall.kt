package digital.upbeat.estisharati_user.UI

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import digital.upbeat.estisharati_user.DataClassHelper.DataCallsFireStore
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.DataUserFireStore
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.CustomTouchListener
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import kotlinx.android.synthetic.main.activity_video_call.*
import kotlinx.android.synthetic.main.activity_video_call.caller_name
import kotlinx.android.synthetic.main.activity_video_call.caller_profile
import kotlinx.android.synthetic.main.activity_video_call.calling_status
import kotlinx.android.synthetic.main.activity_video_call.circle_progress
import kotlinx.android.synthetic.main.activity_video_call.end_call
import kotlinx.android.synthetic.main.activity_video_call.timer
import kotlinx.android.synthetic.main.activity_voice_call.*
import java.util.*
import java.util.concurrent.TimeUnit

class VideoCall : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var mRtcEngine: RtcEngine
    var countDownTimer: CountDownTimer? = null
    var showAction: CountDownTimer? = null
    var ringingDuration: CountDownTimer? = null

    var callConnectedTimeMilles = ""

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
    val iRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, p1: Int) {
            runOnUiThread {
                callConnectedTimeMilles = Date().time.toString()
                if (countDownTimer != null) {
                    countDownTimer?.cancel()
                }
                if(ringingDuration!=null){
                    ringingDuration?.cancel()
                }
                callCountDownTimer()
                calling_status.text = "Connected"
                circle_progress.visibility = View.GONE
                helperMethods.showToastMessage("${dataOtherUserFireStore.fname} join the conversation")
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
                    voice_muted_status.text = "${dataOtherUserFireStore.fname} muted this call"
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
                    video_muted_status.text = "${dataOtherUserFireStore.fname} video paused this call"
                    video_muted_layout.visibility = View.VISIBLE
                } else {
                    video_muted_layout.visibility = View.GONE
                }
                val container = findViewById(R.id.remote_video_view_container) as FrameLayout
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
        helperMethods = HelperMethods(this@VideoCall)
        preferencesHelper = SharedPreferencesHelper(this@VideoCall)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        dataUser = preferencesHelper.getLogInUser()
        firestore = FirebaseFirestore.getInstance()
    }

    fun getDetails() {
        //        if (intent.extras != null) {
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
                val contact_person_id=if(dataCallsFireStore.caller_id.equals(dataUserFireStore.user_id))dataCallsFireStore.receiver_id else dataCallsFireStore.caller_id
                firestore.collection("Users").document(contact_person_id).get().addOnSuccessListener {
                    dataOtherUserFireStore = it.toObject(DataUserFireStore::class.java)!!
                    setDetails()
                }
            }
        }
    }

    fun playRigntone() {
        player = MediaPlayer.create(this, R.raw.ringtone_fbi)
        player?.setLooping(true) // Set looping
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
        Glide.with(this@VideoCall).load(dataOtherUserFireStore.image).apply(helperMethods.profileRequestOption).into(caller_profile)
        caller_name.text = dataOtherUserFireStore.fname + " " + dataOtherUserFireStore.lname
        if (ContextCompat.checkSelfPermission(this@VideoCall, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@VideoCall, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initAgoraEngineAndJoinChannel()
        } else {
            helperMethods.selfPermission(this@VideoCall)
        }

        if (dataCallsFireStore.caller_id.equals(dataUserFireStore.user_id)) {
            playRigntone()
            callRingingDuration()
        }else{
            calling_status.text="Connecting..."
        }
        val hashMap = hashMapOf<String, Any>("availability" to true, "channel_unique_id" to "")
        firestoreRegistrar=  firestore.collection("Calls").document(dataUserFireStore.channel_unique_id).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                dataCallsFireStore = documentSnapshot.toObject(DataCallsFireStore::class.java)!!
                when (dataCallsFireStore.call_status) {
                    "accept" -> {
                    }
                    "reject" -> {
                        firestoreRegistrar.remove()
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage("reject")
                        finish()
                    }
                    "cancel" -> {
                        firestoreRegistrar.remove()
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage("cancel")

                        finish()
                    }
                    "end_call" -> {
                        firestoreRegistrar.remove()
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage("end_call")

                        finish()
                    }
                    "not_responding" -> {
                        firestoreRegistrar.remove()
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage("not_responding")
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
                iv.setColorFilter(ContextCompat.getColor(this@VideoCall, R.color.orange), PorterDuff.Mode.MULTIPLY)
            }
            // Stops/Resumes sending the local video stream.
            mRtcEngine.muteLocalVideoStream(iv.isSelected)
            val container = findViewById(R.id.local_video_view_container) as FrameLayout
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
                iv.setColorFilter(ContextCompat.getColor(this@VideoCall, R.color.orange), PorterDuff.Mode.MULTIPLY)
            }
            // Stops/Resumes sending the local audio stream.
            mRtcEngine.muteLocalAudioStream(iv.isSelected)
        }
        switch_camera.setOnClickListener {
            mRtcEngine.switchCamera()
        }
        end_call.setOnClickListener {
            leaveChannel()
        }

        front_action_layout.setOnClickListener {
            frontAction()
        }

        root.viewTreeObserver.addOnGlobalLayoutListener { local_video_view_container.setOnTouchListener(CustomTouchListener(root.width, root.height)) }
    }

    override fun onDestroy() {
        leaveChannel()
        stopRigntone()
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        if(ringingDuration!=null){
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
        if (ContextCompat.checkSelfPermission(this@VideoCall, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@VideoCall, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initAgoraEngineAndJoinChannel()
        } else {
            finish()
        }
    }
    fun callRingingDuration() {
        var hashMap = hashMapOf<String, Any>()
        val ringingCount=dataCallsFireStore.ringing_duration.toLong()
        ringingDuration= object :CountDownTimer(1000 * ringingCount,1000){
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
        //15 mins
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
            }

            override fun onFinish() {
                callCountDownTimer()
            }
        }.start()
    }

    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), iRtcEngineEventHandler)
        } catch (e: Exception) {
            Log.e("Voice_call", Log.getStackTraceString(e))

            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }
    }

    private fun setupVideoProfile() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo()
        //      mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false) // Earlier than 2.3.0
        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15, VideoEncoderConfiguration.STANDARD_BITRATE, VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT))
    }

    private fun setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        val container = findViewById(R.id.local_video_view_container) as FrameLayout
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        container.addView(surfaceView)
        // Initializes the local video view.
        // RENDER_MODE_FIT: Uniformly scale the video until one of its dimension fits the boundary. Areas that are not filled due to the disparity in the aspect ratio are filled with black.
        mRtcEngine.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        var token: String? = getString(R.string.agora_access_token)
        if (token!!.isEmpty()) {
            token = null
        }
        mRtcEngine.joinChannel(token, dataCallsFireStore.channel_unique_id, "Extra Optional Data", 0) // if you do not specify the uid, we will generate the uid for you
    }

    private fun setupRemoteVideo(uid: Int) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        val container = findViewById(R.id.remote_video_view_container) as FrameLayout

        if (container.childCount >= 1) {
            return
        }
        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly
          calling SurfaceView.
         */
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        container.addView(surfaceView)
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
        surfaceView.tag = uid // for mark purpose
    }

    private fun leaveChannel() {
        var hashMap= hashMapOf<String, Any>()
        if (callConnectedTimeMilles.equals("")) {
             hashMap = hashMapOf("call_status" to "cancel")
        } else {
             hashMap = hashMapOf("call_status" to "end_call")
        }
        helperMethods.updateCallsDetailsToFirestore(dataCallsFireStore.channel_unique_id, hashMap)

        mRtcEngine.leaveChannel()
        RtcEngine.destroy()
    }

    private fun onRemoteUserLeft() {
        val container = findViewById(R.id.remote_video_view_container) as FrameLayout
        container.removeAllViews()
        leaveChannel()
    }
}
