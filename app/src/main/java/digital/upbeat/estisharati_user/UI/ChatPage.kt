package digital.upbeat.estisharati_user.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.util.Log
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.Adapter.ChatAdapter
import digital.upbeat.estisharati_user.DataClassHelper.*
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_chat_page.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.io.IOException
import java.util.*
import retrofit2.Callback
import java.io.File
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatPage : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    var userId = ""
    var dataUserFireStore = DataUserFireStore()
    lateinit var dataUser: DataUser
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var FcmPushretrofitInterface: RetrofitInterface
    var uploadFile: File? = null
    val IdArray = arrayListOf<Int>()
    val messagesArrayList = arrayListOf<DataMessageFireStore>()
    private var chatAdapter: ChatAdapter? = null
    private var recyclerChatViewState: Parcelable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_page)
        initViews()
        clickEvents()
    }

    fun InitializeRecyclerview() {
        chatAdapter?.let {
            chat_recycler.layoutManager?.let {
                  recyclerChatViewState = it.onSaveInstanceState()
            }
        }
        chat_recycler.setHasFixedSize(true)
        chat_recycler.removeAllViews()
        chat_recycler.layoutManager = LinearLayoutManager(this@ChatPage)
        chat_recycler.adapter = ChatAdapter(this@ChatPage, this@ChatPage, messagesArrayList)
        chat_recycler.scrollToPosition(messagesArrayList.size - 1)
        recyclerChatViewState?.let {
            chat_recycler.layoutManager?.onRestoreInstanceState(recyclerChatViewState)
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ChatPage)
        preferencesHelper = SharedPreferencesHelper(this@ChatPage)
        dataUser = preferencesHelper.getLogInUser()
        firestore = FirebaseFirestore.getInstance()
        FcmPushretrofitInterface = RetrofitApiClient("https://fcm.googleapis.com/").getRetrofit().create(RetrofitInterface::class.java)
        intent.extras?.let {
            it.getString("user_id")?.let {
                userId = it
            }
        }
        IdArray.add(dataUser.id.toInt())
        IdArray.add(userId.toInt())
        Collections.sort(IdArray)
        firestoreLisiner()
    }

    fun firestoreLisiner() {
        firestore.collection("Users").document(userId).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                dataUserFireStore = documentSnapshot.toObject(DataUserFireStore::class.java)!!
            }

            name.text = dataUserFireStore.fname + " " + dataUserFireStore.lname
            if (dataUserFireStore.online_status) {
                online_status.text = "Online"
            } else {
                online_status.text = "Last seen " + helperMethods.getFormattedDate(dataUserFireStore.last_seen)
            }
        }
        firestore.collection("Chats").whereEqualTo("communication_id", IdArray).orderBy("send_time", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                messagesArrayList.clear()
                for (data in it) {
                    val messageFireStore = data.toObject(DataMessageFireStore::class.java)
                    messagesArrayList.add(messageFireStore)
                }
                InitializeRecyclerview()
            }
        }
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        voice_call.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@ChatPage, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                if (helperMethods.isConnectingToInternet) {
                    //                                    val intent = Intent(this@ChatPage, VoiceCall::class.java)
                    //                Log.e("channelUniqueId", channelUniqueId)
                    //                intent.putExtra("channelUniqueId", channelUniqueId)
                    //                intent.putExtra("callerId", "1223")
                    //                intent.putExtra("callerName", "testCaller")
                    //                intent.putExtra("callerImage", "https://super-servers.com/estisharati/public/uploads/user_images/JFmmTTkZY3OnDgWOA9w6q3IRuJg4E3mfFFFjQmYL.jpeg")
                    //                startActivity(intent)
                    if (dataUserFireStore.availability) {
                        val channelUniqueId = UUID.randomUUID().toString()
                        val callHashMap = hashMapOf<String, Any>("channel_unique_id" to channelUniqueId, "caller_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "call_type" to "voice_call", "call_status" to "", "ringing_duration" to "60")
                        helperMethods.setCallsDetailsToFirestore(channelUniqueId, callHashMap)
                        val hashMap = hashMapOf<String, Any>("channel_unique_id" to channelUniqueId, "availability" to false)
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataUser.id, hashMap)
                        val data = data("Incoming call", "you are receiving voice call from ${dataUser.fname} ${dataUser.lname}", "incoming_voice_call", dataUser.id, dataUserFireStore.user_id, channelUniqueId)
                        val dataFcmBody = DataFcmBody(dataUserFireStore.fire_base_token, data)
                        sendPushNotification(dataFcmBody)
                    } else {
                        helperMethods.showToastMessage("The person you are calling is busy please try again later")
                    }
                } else {
                    helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                }
            } else {
                helperMethods.selfPermission(this@ChatPage)
            }
        }
        video_call.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@ChatPage, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@ChatPage, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                if (helperMethods.isConnectingToInternet) {
                    if (dataUserFireStore.availability) {
                        val channelUniqueId = UUID.randomUUID().toString()
                        val callHashMap = hashMapOf<String, Any>("channel_unique_id" to channelUniqueId, "caller_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "call_type" to "video_call", "call_status" to "", "ringing_duration" to "60")
                        helperMethods.setCallsDetailsToFirestore(channelUniqueId, callHashMap)
                        val hashMap = hashMapOf<String, Any>("channel_unique_id" to channelUniqueId, "availability" to false)
                        helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                        helperMethods.updateUserDetailsToFirestore(dataUser.id, hashMap)
                        val data = data("Incoming call", "you are receiving video call from ${dataUser.fname} ${dataUser.lname}", "incoming_video_call", dataUser.id, dataUserFireStore.user_id, channelUniqueId)
                        val dataFcmBody = DataFcmBody(dataUserFireStore.fire_base_token, data)
                        sendPushNotification(dataFcmBody)
                    } else {
                        helperMethods.showToastMessage("The person you are calling is busy please try again later")
                    }
                } else {
                    helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                }
            } else {
                helperMethods.selfPermission(this@ChatPage)
            }
        }
        upload_image.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "message_type" to "image", "message_content" to "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQxW0Eeqrx-s7nIpo0PFP_xfop_cKlmqI28fQ&usqp=CAU", "message_status" to "send", "send_time" to FieldValue.serverTimestamp(), "communication_id" to IdArray)
                firestore.collection("Chats").add(hashMap).addOnSuccessListener {}
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        send_msg.setOnClickListener {
            if (sendMessageValidation()) {
                val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "message_type" to "text", "message_content" to message.toText(), "message_status" to "send", "send_time" to FieldValue.serverTimestamp(), "communication_id" to IdArray)
                firestore.collection("Chats").add(hashMap).addOnSuccessListener {
                    message.text = "".toEditable()
                }
            }
        }
    }

    fun sendMessageValidation(): Boolean {
        if (message.toText().isEmpty()) {
            helperMethods.showToastMessage("Enter message before send !")
            return false
        }
        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun sendPushNotification(dataFcmBody: DataFcmBody) {
        helperMethods.showProgressDialog("Please wait while preparing to call...")
        val responseBodyCall = FcmPushretrofitInterface.FCM_SEND_API_CALL(dataFcmBody)
        responseBodyCall.enqueue(object : Callback<okhttp3.ResponseBody> {
            override fun onResponse(call: Call<okhttp3.ResponseBody>, response: retrofit2.Response<okhttp3.ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val success = jsonObject.getString("success")
                            if (success.equals("1")) {
                            } else {
                                Log.d("push_notification", jsonObject.toString())
                                helperMethods.showToastMessage("push notificaiton not set")
                            }

                            if (dataFcmBody.data.tag.equals("incoming_voice_call")) {
                                startActivity(Intent(this@ChatPage, VoiceCall::class.java))
                            } else {
                                startActivity(Intent(this@ChatPage, VideoCall::class.java))
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

            override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}