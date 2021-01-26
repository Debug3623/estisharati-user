package digital.upbeat.estisharati_user.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Chat.DataCallsFireStore
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Chat.DataUserFireStore
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_incoming_call.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class IncomingCall : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    lateinit var dataUserFireStore: DataUserFireStore
    lateinit var dataOtherUserFireStore: DataUserFireStore
    lateinit var dataCallsFireStore: DataCallsFireStore
    var player: MediaPlayer? = null
    lateinit var firestoreRegistrar: ListenerRegistration
    lateinit var retrofitInterface: RetrofitInterface
    var video_balance = 0
    var audio_balance = 0
    var chat_balance = 0
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
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
        firestore = FirebaseFirestore.getInstance()
    }

    fun getDetails() {
        firestore.collection("Users").document(dataUser.id).get().addOnSuccessListener {
            dataUserFireStore = it.toObject(DataUserFireStore::class.java)!!
            firestore.collection("Calls").document(dataUserFireStore.channel_unique_id).get().addOnSuccessListener {
                dataCallsFireStore = it.toObject(DataCallsFireStore::class.java)!!
                getConsultationSecondsApiCall(dataCallsFireStore.caller_id)
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
                            intent.putExtra("audio_balance", audio_balance)
                            startActivity(intent)
                            finish()
                        } else if (dataCallsFireStore.call_type.equals("video_call")) {
                            val intent = Intent(this@IncomingCall, VideoCall::class.java)
                            intent.putExtra("video_balance", video_balance)
                            startActivity(intent)
                            finish()
                        }
                    }
                    "reject" -> {
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage(getString(R.string.your_call_was_not_accepted))
                        finish()
                    }
                    "cancel" -> {
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage(getString(R.string.your_call_was_cancelled))
                        finish()
                    }
                    "end_call" -> {
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataOtherUserFireStore.user_id, hashMap)
                        helperMethods.showToastMessage(getString(R.string.your_call_was_ended))
                        finish()
                    }
                    "not_responding" -> {
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


        Glide.with(this@IncomingCall).load(dataOtherUserFireStore.image).apply(helperMethods.profileRequestOption).into(caller_profile)
        caller_name.text = dataOtherUserFireStore.fname + " " + dataOtherUserFireStore.lname
        if (dataCallsFireStore.call_type.equals("voice_call")) {
            call_type.setBackgroundResource(R.drawable.ic_audio_call)
        } else if (dataCallsFireStore.call_type.equals("video_call")) {
            call_type.setBackgroundResource(R.drawable.ic_video_call)
        }
    }

    fun setAutomaticRejectTime(ringing_duration: String) {
        val time_seconds: Long = ringing_duration.toLong()
        val hours = time_seconds / (60 * 60)
        val tempMint = time_seconds - hours * 60 * 60
        val minutes = tempMint / 60
        val seconds = tempMint - minutes * 60

        Log.d("serviceTimer", "TIME : " + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds))
        auto_ringing_duration.text = "${getString(R.string.automatically_rejected_on)} ${String.format("%02d", minutes) + ":" + String.format("%02d", seconds)} ${getString(R.string.sec)}"
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
            helperMethods.showToastMessage(getString(R.string.we_need_permission_for_this_call))
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

    fun getConsultationSecondsApiCall(consultant_id: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.GET_CONSULTATION_SECONDS_API_CALL("Bearer ${dataUser.access_token}", consultant_id)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val dataString = jsonObject.getString("data")
                                val dataObject = JSONObject(dataString)
                                video_balance = dataObject.getInt("video_balance")
                                audio_balance = dataObject.getInt("audio_balance")
                                chat_balance = dataObject.getInt("chat_balance")
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
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun playRigntone() {
        player = MediaPlayer.create(this, R.raw.ring_ring)
        player?.setLooping(true) // Set looping
        player?.setVolume(100f, 100f)
        player?.start()
    }
}