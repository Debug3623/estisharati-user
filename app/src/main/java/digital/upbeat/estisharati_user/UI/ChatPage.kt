package digital.upbeat.estisharati_user.UI

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.Adapter.ChatAdapter
import digital.upbeat.estisharati_user.DataClassHelper.*
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_chat_page.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.io.IOException
import java.util.*
import retrofit2.Callback
import retrofit2.Response
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
    lateinit var firestoreRegistrar: ListenerRegistration
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
        chatAdapter = ChatAdapter(this@ChatPage, this@ChatPage, messagesArrayList)
        chat_recycler.adapter = chatAdapter
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
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
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
        firestoreRegistrar = firestore.collection("Chats").whereEqualTo("communication_id", IdArray).orderBy("send_time", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                messagesArrayList.clear()
                for (data in it) {
                    val messageFireStore = data.toObject(DataMessageFireStore::class.java)
                    messagesArrayList.add(messageFireStore)
                    if (messageFireStore.receiver_id.equals(dataUser.id)) {
                        if (messageFireStore.message_status.equals("send") || messageFireStore.message_status.equals("delivered")) {
                            val hashMap = hashMapOf<String, Any>("message_status" to "seened")
                            firestore.collection("Chats").document(data.id).update(hashMap).addOnFailureListener {
                                Log.d("FailureListener", "" + it.localizedMessage)
                            }
                        }
                    }
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
            helperMethods.ChangeProfilePhotoPopup(this@ChatPage)
        }
        send_msg.setOnClickListener {
            if (sendMessageValidation()) {
                val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "message_type" to "text", "message_content" to message.toText(), "message_status" to "send", "send_time" to FieldValue.serverTimestamp(), "communication_id" to IdArray)
                firestore.collection("Chats").add(hashMap).addOnSuccessListener {
                    message.text = "".toEditable()
                }.addOnFailureListener {
                    Log.d("FailureListener", "" + it.localizedMessage)
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

    override fun onDestroy() {
        super.onDestroy()
        firestoreRegistrar.remove()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GlobalData.PICK_IMAGE_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val img_uri = data.data
                val filePath = helperMethods.getFilePath(img_uri!!)
                if (filePath == null) {
                    Toast.makeText(this@ChatPage, "Could not get image", Toast.LENGTH_LONG).show()
                    return
                }
                getImageUrlForChatApiCall(filePath)
            }
        } else if (requestCode == GlobalData.PICK_IMAGE_CAMERA) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val yourSelectedImage = data.extras!!.get("data") as Bitmap
                val img_uri = helperMethods.getImageUriFromBitmap(yourSelectedImage)
                val filePath = helperMethods.getFilePath(img_uri)
                if (filePath == null) {
                    Toast.makeText(this@ChatPage, "Could not get image", Toast.LENGTH_LONG).show()
                    return
                }
                getImageUrlForChatApiCall(filePath)
            }
        }
    }

    fun getImageUrlForChatApiCall(filePath: String) {
        val file = File(filePath)
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.getName(), requestBody)

        helperMethods.showProgressDialog("Image uploading...")
        val responseBodyCall = retrofitInterface.upload_chatting_image_API_CALL("Bearer ${dataUser.access_token}", image)
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
                                val image_path = dataObject.getString("image_path")
                                val image_thumb = dataObject.getString("image_thumb")
                                val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "message_type" to "image", "message_content" to image_path, "message_status" to "send", "send_time" to FieldValue.serverTimestamp(), "communication_id" to IdArray)
                                firestore.collection("Chats").add(hashMap).addOnSuccessListener {}
                            } else {
                                val message = jsonObject.getString("message")
                                helperMethods.AlertPopup("Alert", message)
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
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun sendPushNotification(dataFcmBody: DataFcmBody) {
        val body = Gson().toJson(dataFcmBody.data)
        helperMethods.showProgressDialog("Please wait while preparing to call...")
        val responseBodyCall = retrofitInterface.NOTIFY_API_CALL("Bearer ${dataUser.access_token}", dataFcmBody.data.receiver_id, dataFcmBody.data.title, dataFcmBody.data.message, body)
        responseBodyCall.enqueue(object : Callback<okhttp3.ResponseBody> {
            override fun onResponse(call: Call<okhttp3.ResponseBody>, response: retrofit2.Response<okhttp3.ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val dataString = jsonObject.getString("data")
                                val dataObject = JSONObject(dataString)
                                val success = dataObject.getString("success")
                                if (success.equals(1)) {
                                } else {
                                }
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