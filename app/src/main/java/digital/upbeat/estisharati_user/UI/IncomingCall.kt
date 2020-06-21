package digital.upbeat.estisharati_user.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.DataUserFireStore
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_incoming_call.*
import java.lang.Exception

class IncomingCall : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    lateinit var dataUserFireStore: DataUserFireStore
    var player: MediaPlayer? = null
    var channelUniqueId = ""
    var callType = ""
    var callerId = ""
    var callerName = ""
    var callerImage = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
        initViews()
        clickEvents()
        getDetails()

        playRigntone()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@IncomingCall)
        preferencesHelper= SharedPreferencesHelper(this@IncomingCall)
        dataUser=preferencesHelper.getLogInUser()
        firestore= FirebaseFirestore.getInstance()
    }

    fun getDetails() {
        firestore.collection("Users").document(dataUser.id).get().addOnSuccessListener {
            dataUserFireStore=it.toObject(DataUserFireStore::class.java)!!
            firestore.collection(dataUserFireStore.channel_unique_id).get().addOnSuccessListener {

            }
        }

        if (intent.extras != null) {
            channelUniqueId = intent.getStringExtra("channelUniqueId")
            callType = intent.getStringExtra("callType")
            callerId = intent.getStringExtra("callerId")
            callerName = intent.getStringExtra("callerName")
            callerImage = intent.getStringExtra("callerImage")
            setDatils()
        }
    }

    fun setDatils() {

        Glide.with(this@IncomingCall).load(callerImage).apply(helperMethods.profileRequestOption).into(caller_profile)
        caller_name.text = callerName
        if (callType.equals("voice_call")) {
            call_type.setBackgroundResource(R.drawable.ic_audio_call)
        } else if (callType.equals("video_call")) {
            call_type.setBackgroundResource(R.drawable.ic_video_call)
        }
    }

    fun clickEvents() {
        call_accept.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@IncomingCall, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@IncomingCall, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (player!!.isPlaying) {
                        player?.stop()
                        player?.release()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (callType.equals("voice_call")) {
                        val intent = Intent(this@IncomingCall, VoiceCall::class.java)
                        intent.putExtra("channelUniqueId", channelUniqueId)
                        intent.putExtra("callerId", callerId)
                        intent.putExtra("callerName", callerName)
                        intent.putExtra("callerImage", callerImage)
                        startActivity(intent)
                        finish()
                    } else if (callType.equals("video_call")) {
                        val intent = Intent(this@IncomingCall, VideoCall::class.java)
                        intent.putExtra("channelUniqueId", channelUniqueId)
                        intent.putExtra("callerId", callerId)
                        intent.putExtra("callerName", callerName)
                        intent.putExtra("callerImage", callerImage)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                helperMethods.selfPermission(this@IncomingCall)
            }
        }
        call_reject.setOnClickListener {
            try {
                if (player!!.isPlaying) {
                    player?.stop()
                    player?.release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this@IncomingCall, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this@IncomingCall, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            helperMethods.showToastMessage("We need permission for this call!")
        }
        }


    override fun onDestroy() {
        try {
            if (player!!.isPlaying) {
                player?.stop()
                player?.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()

    }
    override fun onBackPressed() {

    }
    fun playRigntone() {
        player = MediaPlayer.create(this, R.raw.ring_ring)
        player?.setLooping(true) // Set looping
        player?.setVolume(100f, 100f)
        player?.start()
    }
}