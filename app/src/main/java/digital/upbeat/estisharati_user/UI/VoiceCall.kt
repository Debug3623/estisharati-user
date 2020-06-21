package digital.upbeat.estisharati_user.UI

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import kotlinx.android.synthetic.main.activity_voice_call.*
import java.util.*
import java.util.concurrent.TimeUnit

class VoiceCall : AppCompatActivity(), SensorEventListener {
    var player: MediaPlayer? = null
    lateinit var mRtcEngine: RtcEngine
    var enableSpeakerphone = false
    var muteLocalAudioStrea = false
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    var countDownTimer: CountDownTimer? = null
    var callConnectedTimeMilles = ""
    var channelUniqueId = ""
    var callerId = ""
    var callerName = ""
    var callerImage = ""
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    val iRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserOffline(uid: Int, reason: Int) { // Tutorial Step 4
            runOnUiThread {
                helperMethods.showToastMessage("$callerName left the conversation")
                endTheCall()
            }
        }

        override fun onUserMuteAudio(uid: Int, muted: Boolean) {
            runOnUiThread {
                if (muted) {
                    muted_status.text = "$callerName muted this call"
                    muted_status.visibility = View.VISIBLE
                } else {
                    muted_status.visibility = View.GONE
                }
            }
            // Tutorial Step 6
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                callConnectedTimeMilles = Date().time.toString()
                if (countDownTimer != null) {
                    countDownTimer?.cancel()
                }
                callCountDownTimer()
                calling_status.text = "Connected"
                circle_progress.visibility = View.GONE
                stopRigntone()

                helperMethods.showToastMessage("$callerName join the conversation")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_call)
        initViews()
        getDetails()
        clickEvents()
        object : CountDownTimer(2000, 1000) {
            override fun onFinish() {
                if (callConnectedTimeMilles.equals("")) {
                    playRigntone()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
            }
        }.start()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@VoiceCall)
        preferencesHelper = SharedPreferencesHelper(this@VoiceCall)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
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

    fun getDetails() {
        if (intent.extras != null) {
            channelUniqueId = intent.getStringExtra("channelUniqueId")
            callerId = intent.getStringExtra("callerId")
            callerName = intent.getStringExtra("callerName")
            callerImage = intent.getStringExtra("callerImage")
            setDatils()
            if (ContextCompat.checkSelfPermission(this@VoiceCall, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                initAgoraEngineAndJoinChannel()
            } else {
                helperMethods.selfPermission(this@VoiceCall)
            }
        }
    }

    fun setDatils() {
        Glide.with(this@VoiceCall).load(callerImage).apply(helperMethods.profileRequestOption).into(caller_profile)
        caller_name.text = callerName
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

    fun endTheCall() {
        mRtcEngine.leaveChannel()
        RtcEngine.destroy()
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
        finish()
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
                iv.setColorFilter(ContextCompat.getColor(this@VoiceCall, R.color.orange), PorterDuff.Mode.MULTIPLY)
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
                iv.setColorFilter(ContextCompat.getColor(this@VoiceCall, R.color.orange), PorterDuff.Mode.MULTIPLY)
            }
            mRtcEngine.muteLocalAudioStream(muteLocalAudioStrea)
        }
    }

    private fun initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine() // Tutorial Step 1
        joinChannel() // Tutorial Step 2
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (ContextCompat.checkSelfPermission(this@VoiceCall, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            initAgoraEngineAndJoinChannel()
        } else {
            helperMethods.showToastMessage("Audio permission denied")
            finish()
        }
    }

    // Tutorial Step 1
    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), iRtcEngineEventHandler)
            // Sets the channel profile of the Agora RtcEngine.
            // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
            // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
            //            mRtcEngine.setEnableSpeakerphone(enableSpeakerphone)
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
        }
        // Allows a user to join a channel.
        mRtcEngine.joinChannel(accessToken, "demoChannel2", "Extra Optional Data", 0) // if you do not specify the uid, we will generate the uid for you
    }

    override fun onBackPressed() {
    }

    override fun onResume() {
        // Register a listener for the sensor.
        super.onResume()

        sensor?.also { proximity ->
            sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRigntone()
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
        val childCount: Int = viewGroup.getChildCount()
        for (i in 0 until childCount) {
            val view: View = viewGroup.getChildAt(i)
            view.isEnabled = enabled
            if (view is ViewGroup) {
                enableDisableViewGroup(view as ViewGroup, enabled)
            }
        }
    }
}