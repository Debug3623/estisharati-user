package digital.upbeat.estisharati_user.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_incoming_call.*

class IncomingCall : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    lateinit var dataUserFireStore: DataUserFireStore
    lateinit var dataOtherUserFireStore: DataUserFireStore
    lateinit var dataCallsFireStore: DataCallsFireStore
    var player: MediaPlayer? = null
    lateinit var firestoreRegistrar: ListenerRegistration
    //    var channelUniqueId = ""
    //    var callType = ""
    //    var callerId = ""
    //    var callerName = ""
    //    var callerImage = ""
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
        preferencesHelper = SharedPreferencesHelper(this@IncomingCall)
        dataUser = preferencesHelper.getLogInUser()
        firestore = FirebaseFirestore.getInstance()
    }

    fun getDetails() {
        firestore.collection("Users").document(dataUser.id).get().addOnSuccessListener {
            dataUserFireStore = it.toObject(DataUserFireStore::class.java)!!
            firestore.collection("Calls").document(dataUserFireStore.channel_unique_id).get().addOnSuccessListener {
                dataCallsFireStore = it.toObject(DataCallsFireStore::class.java)!!
                firestore.collection("Users").document(dataCallsFireStore.caller_id).get().addOnSuccessListener {
                    dataOtherUserFireStore = it.toObject(DataUserFireStore::class.java)!!
                    setDetails()
                }
            }
        }
        //        if (intent.extras != null) {
        //            channelUniqueId = intent.getStringExtra("channelUniqueId")
        //            callType = intent.getStringExtra("callType")
        //            callerId = intent.getStringExtra("callerId")
        //            callerName = intent.getStringExtra("callerName")
        //            callerImage = intent.getStringExtra("callerImage")
        //            setDatils()
        //        }
    }

    fun setDetails() {

        val hashMap = hashMapOf<String, Any>("availability" to true, "channel_unique_id" to "")
        firestoreRegistrar = firestore.collection("Calls").document(dataUserFireStore.channel_unique_id).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                dataCallsFireStore = documentSnapshot.toObject(DataCallsFireStore::class.java)!!
                setAutomaticRejectTime(dataCallsFireStore.ringing_duration)
                 when (dataCallsFireStore.call_status) {
                    "accept" -> {
                        firestoreRegistrar.remove()
                        if (dataCallsFireStore.call_type.equals("voice_call")) {
                            val intent = Intent(this@IncomingCall, VoiceCall::class.java)
                            startActivity(intent)
                            finish()
                        } else if (dataCallsFireStore.call_type.equals("video_call")) {
                            val intent = Intent(this@IncomingCall, VideoCall::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    "reject" -> {
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage("reject")
                        finish()
                    }
                    "cancel" -> {
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage("cancel")
                        finish()
                    }
                    "end_call" -> {
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage("end_call")
                        finish()
                    }
                    "not_responding" -> {
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


        Glide.with(this@IncomingCall).load(dataOtherUserFireStore.image).apply(helperMethods.profileRequestOption).into(caller_profile)
        caller_name.text = dataOtherUserFireStore.fname + " " + dataOtherUserFireStore.lname
        if (dataCallsFireStore.call_type.equals("voice_call")) {
            call_type.setBackgroundResource(R.drawable.ic_audio_call)
        } else if (dataCallsFireStore.call_type.equals("video_call")) {
            call_type.setBackgroundResource(R.drawable.ic_video_call)
        }
    }

    fun setAutomaticRejectTime( ringing_duration:String){
        val time_seconds: Long = ringing_duration.toLong()
        val hours = time_seconds / (60 * 60)
        val tempMint = time_seconds - hours * 60 * 60
        val minutes = tempMint / 60
        val seconds = tempMint - minutes * 60

        Log.d("serviceTimer", "TIME : " + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds))
        auto_ringing_duration.text = "Automatically rejected on ${String.format("%02d", minutes) + ":" + String.format("%02d", seconds)} sec"

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
                    val hashMap = hashMapOf<String, Any>("call_status" to "accept")
                    helperMethods.updateCallsDetailsToFirestore(dataCallsFireStore.channel_unique_id, hashMap)
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
                val hashMap = hashMapOf<String, Any>("call_status" to "reject")
                helperMethods.updateCallsDetailsToFirestore(dataCallsFireStore.channel_unique_id, hashMap)
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