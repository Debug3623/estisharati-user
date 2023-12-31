package estisharatibussiness.users.com.UserInterfaces

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import estisharatibussiness.users.com.AdapterClasses.ChatAdapter
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataMessageFireStore
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataUserFireStore
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.SendNotification.DataFcmBody
import estisharatibussiness.users.com.DataClassHelperMehtods.SendNotification.data
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.MessageSwiper.MessageSwipeController
import estisharatibussiness.users.com.MessageSwiper.SwipeControllerActions
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_chat_page.*
import kotlinx.android.synthetic.main.chat_action_alert.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*

class ChatPageActivity : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    var userId = ""
    var forward_type = ""
    var forward_content = ""
    var dataUserFireStore = DataUserFireStore()
    var currentUserFireStore = DataUserFireStore()
    lateinit var dataUser: DataUser
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var FcmPushretrofitInterface: RetrofitInterface
    var uploadFile: File? = null
    val IdArray = arrayListOf<Int>()
    val messagesArrayList = arrayListOf<DataMessageFireStore>()
    var inside_reply = hashMapOf<String, String>()
    private var chatAdapter: ChatAdapter? = null
    private var recyclerChatViewState: Parcelable? = null
    lateinit var firestoreRegistrar: ListenerRegistration
    var slide_right: Animation? = null
    var slide_left: Animation? = null
    var slide_top: Animation? = null
    var slide_bottom: Animation? = null
    var video_balance = 0
    var audio_balance = 0
    var chat_balance = 0
    lateinit var pickiT: PickiT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_page)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ChatPageActivity)
        preferencesHelper = SharedPreferencesHelper(this@ChatPageActivity)
        dataUser = preferencesHelper.logInUser
        firestore = FirebaseFirestore.getInstance()
        slide_right = AnimationUtils.loadAnimation(this@ChatPageActivity, R.anim.slide_right)
        slide_left = AnimationUtils.loadAnimation(this@ChatPageActivity, R.anim.slide_left)
        slide_top = AnimationUtils.loadAnimation(this@ChatPageActivity, R.anim.slide_top)
        slide_bottom = AnimationUtils.loadAnimation(this@ChatPageActivity, R.anim.slide_bottom)

        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        FcmPushretrofitInterface = RetrofitApiClient("https://fcm.googleapis.com/").getRetrofit().create(RetrofitInterface::class.java)
        intent.extras?.let {
            it.getString("user_id")?.let {
                userId = it
            }
            it.getString("forward_type")?.let {
                if (!it.equals("")) {
                    forward_type = it
                }
            }
            it.getString("forward_content")?.let {
                if (!it.equals("")) {
                    forward_content = it
                }
            }
        }
        IdArray.add(dataUser.id.toInt())
        IdArray.add(userId.toInt())
        IdArray.sort()
//        Collections.sort(IdArray)

        firestoreUserLisiner()
        firestoreChatLisiner()

        pickiT = PickiT(this, object : PickiTCallbacks {
            override fun PickiTonUriReturned() {
            }

            override fun PickiTonStartListener() {
            }

            override fun PickiTonProgressUpdate(progress: Int) {
            }

            override fun PickiTonCompleteListener(filePath: String?, wasDriveFile: Boolean, wasUnknownProvider: Boolean, wasSuccessful: Boolean, Reason: String?) {
                if (filePath == null && !wasSuccessful) {
                    Toast.makeText(this@ChatPageActivity, getString(R.string.could_not_get_image), Toast.LENGTH_LONG).show()
                    return
                }
                filePath?.let {
                    Log.d("path", filePath + "")
                    getImageUrlForChatApiCall(filePath)
                }
            }

            override fun PickiTonMultipleCompleteListener(paths: ArrayList<String>?, wasSuccessful: Boolean, Reason: String?) {
            }
        }, this)


    }

    override fun onStart() {
        UpdateConsultationSecondsApiCall("", userId, "0", "", "")
        super.onStart()
    }

    fun InitializeRecyclerview() {
        chatAdapter?.let {
            chat_recycler.layoutManager?.let {
                recyclerChatViewState = it.onSaveInstanceState()
            }
        }
        chat_recycler.setHasFixedSize(true)
        chat_recycler.removeAllViews()
        chat_recycler.layoutManager = LinearLayoutManager(this@ChatPageActivity)
        chatAdapter = ChatAdapter(this@ChatPageActivity, this@ChatPageActivity, messagesArrayList)
        chat_recycler.adapter = chatAdapter
        chat_recycler.scrollToPosition(messagesArrayList.size - 1)
        recyclerChatViewState?.let {
            chat_recycler.layoutManager?.onRestoreInstanceState(recyclerChatViewState)
        }
        val messageSwipeController = MessageSwipeController(this, object : SwipeControllerActions {
            override fun showReplyUI(position: Int) {
                insideReply(position)
            }
        })
        val itemTouchHelper = ItemTouchHelper(messageSwipeController)
        itemTouchHelper.attachToRecyclerView(chat_recycler)
        chat_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                var position: Int = (chat_recycler.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                position++
                Log.e("position", position.toString() + "          " + messagesArrayList.size)
                if (newState === RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (messagesArrayList.size > 10) {
                        showChatScrollToBottom(position)
                    }
                } else if (newState === RecyclerView.SCROLL_STATE_IDLE) {
                    if (messagesArrayList.size > 10) {
                        showChatScrollToBottom(position)
                    }
                }
            }
        })
        chat_scroll_to_bottom.setOnClickListener {
            showChatScrollToBottom(messagesArrayList.size)
            chat_recycler.scrollToPosition(messagesArrayList.size - 1)
        }
    }

    fun showChatScrollToBottom(position: Int) {
        if (position >= (messagesArrayList.size - 3)) {
            if (chat_scroll_to_bottom.visibility == View.VISIBLE) {
                chat_scroll_to_bottom.startAnimation(slide_bottom)
                chat_scroll_to_bottom.visibility = View.GONE
            }
        } else {
            if (chat_scroll_to_bottom.visibility == View.GONE) {
                chat_scroll_to_bottom.startAnimation(slide_top)
                chat_scroll_to_bottom.visibility = View.VISIBLE
            }
        }
    }



    fun firestoreUserLisiner() {
        firestore.collection("Users").document(userId).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                if (documentSnapshot.toObject(DataUserFireStore::class.java) != null) {
                    dataUserFireStore = documentSnapshot.toObject(DataUserFireStore::class.java)!!
                } else {
                    helperMethods.showToastMessage(getString(R.string.currently_the_consultant_is_not_available_for_chat))
                    finish()
                }
            }

            name.text = dataUserFireStore.fname + " " + dataUserFireStore.lname
            if (dataUserFireStore.online_status) {
                online_status.text = getString(R.string.online)
            } else {
                online_status.text = getString(R.string.last_seen) + " " + helperMethods.getFormattedDate(dataUserFireStore.last_seen)
            }
        }
        firestore.collection("Users").document(dataUser.id).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                if (documentSnapshot.toObject(DataUserFireStore::class.java) != null) {
                    currentUserFireStore = documentSnapshot.toObject(DataUserFireStore::class.java)!!
                }
            }
        }
    }

    fun firestoreChatLisiner() {
        firestoreRegistrar = firestore.collection("Chats").whereEqualTo("communication_id", IdArray).orderBy("send_time", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                messagesArrayList.clear()
                for (data in it) {
                    val messageFireStore = data.toObject(DataMessageFireStore::class.java)
                    messageFireStore.message_id = data.id
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
            if (audio_balance > 0) {
                if (ContextCompat.checkSelfPermission(this@ChatPageActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    if (helperMethods.isConnectingToInternet) {
                        if (dataUserFireStore.availability) {
                            if (currentUserFireStore.blocked_user_ids.contains(userId) || dataUserFireStore.blocked_user_ids.contains(dataUser.id)) {
                                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.chat_has_been_blocked))
                                return@setOnClickListener
                            }
                            val channelUniqueId = UUID.randomUUID().toString()
                            val callHashMap = hashMapOf<String, Any>("channel_unique_id" to channelUniqueId, "caller_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "call_type" to "voice_call", "call_status" to "", "ringing_duration" to "60")
                            helperMethods.setCallsDetailsToFirestore(channelUniqueId, callHashMap)
                            val hashMap = hashMapOf<String, Any>("channel_unique_id" to channelUniqueId, "availability" to false)
                            helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                            helperMethods.updateUserDetailsToFirestore(dataUser.id, hashMap)
                            val data = data(getString(R.string.incoming_call), "${getString(R.string.you_are_receiving_voice_call_from)} ${dataUser.fname} ${dataUser.lname}", "incoming_voice_call", dataUser.id, dataUserFireStore.user_id, channelUniqueId, "")
                            val dataFcmBody = DataFcmBody("", data)
                            sendPushNotification(dataFcmBody, true)
                        } else {
                            helperMethods.showToastMessage(getString(R.string.the_person_you_are_calling_is_busy_please_try_again_later))
                        }
                    } else {
                        helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                    }
                } else {
                    helperMethods.selfPermission(this@ChatPageActivity)
                }
            } else {
                helperMethods.showToastMessage(getString(R.string.you_dont_have_enough_balance_to_make_call))
            }
        }
        video_call.setOnClickListener {
            if (video_balance > 0) {
                if (ContextCompat.checkSelfPermission(this@ChatPageActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@ChatPageActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if (helperMethods.isConnectingToInternet) {
                        if (dataUserFireStore.availability) {
                            if (currentUserFireStore.blocked_user_ids.contains(userId) || dataUserFireStore.blocked_user_ids.contains(dataUser.id)) {
                                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.chat_has_been_blocked))
                                return@setOnClickListener
                            }
                            val channelUniqueId = UUID.randomUUID().toString()
                            val callHashMap = hashMapOf<String, Any>("channel_unique_id" to channelUniqueId, "caller_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "call_type" to "video_call", "call_status" to "", "ringing_duration" to "60")
                            helperMethods.setCallsDetailsToFirestore(channelUniqueId, callHashMap)
                            val hashMap = hashMapOf<String, Any>("channel_unique_id" to channelUniqueId, "availability" to false)
                            helperMethods.updateUserDetailsToFirestore(dataUserFireStore.user_id, hashMap)
                            helperMethods.updateUserDetailsToFirestore(dataUser.id, hashMap)
                            val data = data(getString(R.string.incoming_call), "${getString(R.string.you_are_receiving_video_call_from)} ${dataUser.fname} ${dataUser.lname}", "incoming_video_call", dataUser.id, dataUserFireStore.user_id, channelUniqueId, "")
                            val dataFcmBody = DataFcmBody("", data)
                            sendPushNotification(dataFcmBody, true)
                        } else {
                            helperMethods.showToastMessage(getString(R.string.the_person_you_are_calling_is_busy_please_try_again_later))
                        }
                    } else {
                        helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                    }
                } else {
                    helperMethods.selfPermission(this@ChatPageActivity)
                }
            } else {
                helperMethods.showToastMessage(getString(R.string.you_dont_have_enough_balance_to_make_call))
            }
        }
        upload_image.setOnClickListener {
            if (chat_balance > 0) {
                if (currentUserFireStore.blocked_user_ids.contains(userId) || dataUserFireStore.blocked_user_ids.contains(dataUser.id)) {
                    helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.chat_has_been_blocked))
                    return@setOnClickListener
                }
                helperMethods.ChangeProfilePhotoPopup(this@ChatPageActivity)
            } else {
                helperMethods.showToastMessage(getString(R.string.you_dont_have_enough_balance_to_make_this_chat))
            }
        }
        send_msg.setOnClickListener {
            if (sendMessageValidation()) {
                if (chat_balance >  0) {
                    if (currentUserFireStore.blocked_user_ids.contains(userId) || dataUserFireStore.blocked_user_ids.contains(dataUser.id)) {
                        helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.chat_has_been_blocked))
                        return@setOnClickListener
                    }
                    val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "message_type" to "text", "message_content" to message.toText(), "message_status" to "send", "message_other_type" to "normal", "send_time" to FieldValue.serverTimestamp(), "communication_id" to IdArray, "inside_reply" to inside_reply)
                    firestore.collection("Chats").add(hashMap).addOnSuccessListener {
                        inside_reply_layout.visibility = View.GONE
                        inside_reply.put("message_id", "")
                        inside_reply.put("message_type", "")
                        inside_reply.put("message_content", "")
                        inside_reply.put("sender_id", "")
                        inside_reply.put("position", "")
                        val words: String = message.toText()
                        val count = words.toCharArray().size-(words.lines().size-1)
                        Log.d("count", "" + count)
                        UpdateConsultationSecondsApiCall(it.id, dataUserFireStore.user_id, count.toString(), message.toText(), "")
                        message.text = "".toEditable()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                        finish()
//                       chatAdapter!!.notifyDataSetChanged()



                    }.addOnFailureListener {
                        Log.d("FailureListener", "" + it.localizedMessage)
                    }
                } else {
                    helperMethods.showToastMessage(getString(R.string.you_dont_have_enough_balance_to_make_this_chat))
                }
            }
        }
        inside_reply_close.setOnClickListener {
            inside_reply.put("message_id", "")
            inside_reply.put("message_type", "")
            inside_reply.put("message_content", "")
            inside_reply.put("sender_id", "")
            inside_reply.put("position", "")
            inside_reply_layout.visibility = View.GONE
        }

        dotsMenu.setOnClickListener { showSettingsPopup() }
    }

    fun sendMessageValidation(): Boolean {
        if (message.toText().isEmpty()) {
            helperMethods.showToastMessage(getString(R.string.enter_message_before_send))
            return false
        }
        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun insideReply(position: Int) {
        helperMethods.showKeyboard(message)
        val dataMessageFireStore = messagesArrayList.get(position)
        inside_reply.put("message_id", dataMessageFireStore.message_id)
        inside_reply.put("message_type", dataMessageFireStore.message_type)
        inside_reply.put("message_content", dataMessageFireStore.message_content)
        inside_reply.put("sender_id", dataMessageFireStore.sender_id)
        inside_reply.put("position", position.toString())
        inside_reply_layout.visibility = View.VISIBLE
        if (dataMessageFireStore.sender_id.equals(dataUser.id)) {
            inside_reply_from.text = getString(R.string.you)
            inside_reply_from.setTextColor(ContextCompat.getColor(this@ChatPageActivity, R.color.green))
        } else {
            inside_reply_from.text = dataUserFireStore.fname + " " + dataUserFireStore.lname
            inside_reply_from.setTextColor(ContextCompat.getColor(this@ChatPageActivity, R.color.orange))
        }
        if (dataMessageFireStore.message_type.equals("text")) {
            inside_reply_text.text = dataMessageFireStore.message_content
            inside_reply_text.visibility = View.VISIBLE
            inside_reply_image_layout.visibility = View.GONE
        } else if (dataMessageFireStore.message_type.equals("image")) {
            Glide.with(this@ChatPageActivity).load(dataMessageFireStore.message_content).apply(helperMethods.requestOption).into(inside_reply_image)
            inside_reply_image_layout.visibility = View.VISIBLE
            inside_reply_text.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreRegistrar.remove()
        pickiT.deleteTemporaryFile(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        pickiT.deleteTemporaryFile(this)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GlobalData.PICK_IMAGE_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                pickiT.getPath(data.data, Build.VERSION.SDK_INT)
            }
        } else if (requestCode == GlobalData.PICK_IMAGE_CAMERA) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val yourSelectedImage = data.extras!!.get("data") as Bitmap
                val img_uri = helperMethods.getImageUriFromBitmap(yourSelectedImage)
                pickiT.getPath(img_uri, Build.VERSION.SDK_INT)
            }
        }
    }

    fun getImageUrlForChatApiCall(filePath: String) {
        val file = File(filePath)
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.name, requestBody)

        helperMethods.showProgressDialog(getString(R.string.image_uploading))
        val responseBodyCall = retrofitInterface.UPLOAD_CHATTING_IMAGE_API_CALL("Bearer ${dataUser.access_token}", image)
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
                                val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "receiver_id" to dataUserFireStore.user_id, "message_type" to "image", "message_content" to image_path, "message_status" to "send", "message_other_type" to "normal", "send_time" to FieldValue.serverTimestamp(), "communication_id" to IdArray, "inside_reply" to inside_reply)
                                firestore.collection("Chats").add(hashMap).addOnSuccessListener {
                                    inside_reply_layout.visibility = View.GONE
                                    inside_reply.put("message_id", "")
                                    inside_reply.put("message_type", "")
                                    inside_reply.put("message_content", "")
                                    inside_reply.put("sender_id", "")
                                    inside_reply.put("position", "")
                                    UpdateConsultationSecondsApiCall(it.id, dataUserFireStore.user_id, "1", "", image_path)
                                    overridePendingTransition(0, 0)
                                    startActivity(intent)
                                    overridePendingTransition(0, 0)
                                    finish()

                                }.addOnFailureListener {
                                    Log.d("FailureListener", "" + it.localizedMessage)
                                }
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

    fun UpdateConsultationSecondsApiCall(
        message_id: String,
        consultant_id: String,
        chat_count: String,
        message: String,
        imageUrl: String,
    ) { //        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = if (chat_count.toInt() > 0) retrofitInterface.UPDATE_CONSULTATION_SECONDS_API_CALL("Bearer ${dataUser.access_token}", consultant_id, "0", "0", chat_count, consultant_id, message, imageUrl)
        else retrofitInterface.GET_CONSULTATION_SECONDS_API_CALL("Bearer ${dataUser.access_token}", consultant_id)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) { //                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            Log.d("jsonObject", jsonObject.toString())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val dataString = jsonObject.getString("data")
                                val dataObject = JSONObject(dataString)
                                video_balance = dataObject.getInt("video_balance")
                                audio_balance = dataObject.getInt("audio_balance") //                                chat_balance = 0
                                chat_balance = dataObject.getInt("chat_balance")
                                if (!forward_content.isEmpty()) {
                                    if (chat_balance > 0) {
                                        if (currentUserFireStore.blocked_user_ids.contains(userId) || dataUserFireStore.blocked_user_ids.contains(dataUser.id)) {
                                            helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.chat_has_been_blocked))
                                            return
                                        }
                                        val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "receiver_id" to userId, "message_type" to forward_type, "message_content" to forward_content, "message_status" to "send", "message_other_type" to "forwarded", "send_time" to FieldValue.serverTimestamp(), "communication_id" to IdArray, "inside_reply" to inside_reply)
                                        firestore.collection("Chats").add(hashMap).addOnSuccessListener { // ********for empty inside reply message*********
                                            inside_reply.put("message_id", "")
                                            inside_reply.put("message_type", "")
                                            inside_reply.put("message_content", "")
                                            inside_reply.put("sender_id", "")
                                            inside_reply.put("position", "")
                                            var itsMessage = ""
                                            var itsImageUrl = ""
                                            var count = 1
                                            if (forward_type.equals("image")) {
                                                itsImageUrl = forward_content
                                            } else if (forward_type.equals("text")) {
                                                itsMessage = forward_content
                                                val words: String = itsMessage
                                                count = words.toCharArray().size-(words.lines().size-1)
                                                Log.d("count", "" + count)
                                            }
                                            forward_content = ""
                                            UpdateConsultationSecondsApiCall(it.id, dataUserFireStore.user_id, count.toString(), itsMessage, itsImageUrl)
                                        }.addOnFailureListener {
                                            Log.d("FailureListener", "" + it.localizedMessage)
                                        }
                                    } else {
                                        helperMethods.showToastMessage(getString(R.string.you_dont_have_enough_balance_to_make_this_chat))
                                    }
                                } else {
                                    if (message_id != "") {
                                        firestore.collection("Chats").document(message_id).get().addOnSuccessListener {
                                            val messageFireStore = it.toObject(DataMessageFireStore::class.java)
                                            messageFireStore?.let {
                                                when (messageFireStore.message_status) {
                                                    "send" -> {
                                                        var messageText = ""
                                                        var messageImageURl = ""
                                                        var dataMessage: data? = null
                                                        if (messageFireStore.message_type.equals("text")) {
                                                            messageText = messageFireStore.message_content
                                                            dataMessage = data("New message", "${dataUser.fname} send message : ${messageText}", "incoming_message", dataUser.id, dataUserFireStore.user_id, "", "")
                                                        } else if (messageFireStore.message_type.equals("image")) {
                                                            messageImageURl = messageFireStore.message_content
                                                            dataMessage = data("New message", "${dataUser.fname} send image ", "incoming_message", dataUser.id, dataUserFireStore.user_id, "", messageImageURl)
                                                        }
                                                        val dataFcmBody = DataFcmBody("", dataMessage!!)
                                                        sendPushNotification(dataFcmBody, false)
                                                    }
                                                    "delivered" -> {
                                                    }
                                                    "seened" -> {
                                                    }
                                                    else -> {
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                val message_ = jsonObject.getString("message")
                                if (helperMethods.checkTokenValidation(status, message_)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), message_)
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

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { //                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun sendPushNotification(dataFcmBody: DataFcmBody, showLoader: Boolean) {
        val body = Gson().toJson(dataFcmBody.data)
        Log.d("body", body)
        if (showLoader) {
            helperMethods.showProgressDialog(getString(R.string.please_wait_while_preparing_to_call))
        }
        var clickAction = ""
        if (dataFcmBody.data.tag == "incoming_message") {
            clickAction = "NotificationClick"
        }
        val responseBodyCall = retrofitInterface.NOTIFY_API_CALL("Bearer ${dataUser.access_token}", dataFcmBody.data.receiver_id, dataFcmBody.data.title, dataFcmBody.data.body, clickAction, body)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (showLoader) {
                    helperMethods.dismissProgressDialog()
                }
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val dataString = jsonObject.getString("data")
                                val dataObject = JSONObject(dataString)
                                val success = dataObject.getString("success")
                            } else {
                                if (helperMethods.checkTokenValidation(status, "")) {
                                    finish()
                                    return
                                }
                                Log.d("push_notification", jsonObject.toString())
                                helperMethods.showToastMessage(getString(R.string.push_notificaiton_not_send))
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
                if (dataFcmBody.data.tag.equals("incoming_voice_call")) {
                    val intent = Intent(this@ChatPageActivity, VoiceCallActivity::class.java)
                    intent.putExtra("audio_balance", audio_balance)
                    startActivity(intent)
                } else if (dataFcmBody.data.tag.equals("incoming_video_call")) {
                    val intent = Intent(this@ChatPageActivity, VideoCallActivity::class.java)
                    intent.putExtra("video_balance", video_balance)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (showLoader) {
                    helperMethods.dismissProgressDialog()
                }
                t.printStackTrace()
                if (dataFcmBody.data.tag.equals("incoming_voice_call")) {
                    val intent = Intent(this@ChatPageActivity, VoiceCallActivity::class.java)
                    intent.putExtra("audio_balance", audio_balance)
                    startActivity(intent)
                } else if (dataFcmBody.data.tag.equals("incoming_video_call")) {
                    val intent = Intent(this@ChatPageActivity, VideoCallActivity::class.java)
                    intent.putExtra("video_balance", video_balance)
                    startActivity(intent)
                }
                helperMethods.showToastMessage(getString(R.string.oops_notification_sending_problem))
            }
        })
    }

    fun showSettingsPopup() {
        val layoutView = LayoutInflater.from(this@ChatPageActivity).inflate(R.layout.chat_action_alert, null)
        val aleatdialog = AlertDialog.Builder(this@ChatPageActivity)
        aleatdialog.setView(layoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        if (currentUserFireStore.blocked_user_ids.contains(userId)) {
            layoutView.blockUnBlockChat.text = getString(R.string.unblock_chat)
        } else {
            layoutView.blockUnBlockChat.text = getString(R.string.block_chat)
        }
        layoutView.reportUser.setOnClickListener {
            val intent = Intent(this@ChatPageActivity, ActivityContactUs::class.java)
            intent.putExtra("userId", dataUserFireStore.user_id)
            intent.putExtra("userName", dataUserFireStore.fname + " " + dataUserFireStore.lname)
            startActivity(intent)
            dialog.dismiss()
        }
        layoutView.reportViolationContent.setOnClickListener {
            val intent = Intent(this@ChatPageActivity, ActivityContactUs::class.java)
            intent.putExtra("userId", dataUserFireStore.user_id)
            intent.putExtra("userName", dataUserFireStore.fname + " " + dataUserFireStore.lname)
            startActivity(intent)
            dialog.dismiss()
        }
        layoutView.blockUnBlockChat.setOnClickListener {
            if (currentUserFireStore.blocked_user_ids.contains(dataUserFireStore.user_id)) {
                currentUserFireStore.blocked_user_ids.remove(dataUserFireStore.user_id)
            } else {
                currentUserFireStore.blocked_user_ids.add(dataUserFireStore.user_id)
            }
            val hashMap = hashMapOf<String, Any>("blocked_user_ids" to currentUserFireStore.blocked_user_ids)
            helperMethods.updateUserDetailsToFirestore(currentUserFireStore.user_id, hashMap)
            dialog.dismiss()
            firestoreUserLisiner()
        }
        layoutView.actionCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()


}
