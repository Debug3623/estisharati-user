package digital.upbeat.estisharati_user.UI

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import digital.upbeat.estisharati_user.Adapter.GroupChatAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.*
import digital.upbeat.estisharati_user.DataClassHelper.Chat.DataUserFireStore
import digital.upbeat.estisharati_user.DataClassHelper.ChatGroupMessage.GroupMessageFireStore
import digital.upbeat.estisharati_user.DataClassHelper.Group.GroupItem
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.MessageSwipe.MessageSwipeController
import digital.upbeat.estisharati_user.MessageSwipe.SwipeControllerActions
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_group_chat_page.*
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

class GroupChatPage : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    var group_id = ""
    var forward_type = ""
    var forward_content = ""
    lateinit var dataUser: DataUser
    lateinit var groupItemData: GroupItem
    lateinit var retrofitInterface: RetrofitInterface
    var userArraylist = arrayListOf<DataUserFireStore>()
    val messagesArrayList = arrayListOf<GroupMessageFireStore>()
    var inside_reply = hashMapOf<String, String>()
    private var chatAdapter: GroupChatAdapter? = null
    private var recyclerChatViewState: Parcelable? = null
    lateinit var firestoreRegistrar: ListenerRegistration
    var slide_right: Animation? = null
    var slide_left: Animation? = null
    var slide_top: Animation? = null
    var slide_bottom: Animation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat_page)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@GroupChatPage)
        preferencesHelper = SharedPreferencesHelper(this@GroupChatPage)
        dataUser = preferencesHelper.logInUser
        firestore = FirebaseFirestore.getInstance()
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        slide_right = AnimationUtils.loadAnimation(this@GroupChatPage, R.anim.slide_right)
        slide_left = AnimationUtils.loadAnimation(this@GroupChatPage, R.anim.slide_left)
        slide_top = AnimationUtils.loadAnimation(this@GroupChatPage, R.anim.slide_top)
        slide_bottom = AnimationUtils.loadAnimation(this@GroupChatPage, R.anim.slide_bottom)

        intent.extras?.let {
            it.getString("group_id")?.let {
                group_id = it
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

        firestoreLisiner()
        if (!forward_content.isEmpty()) {
            val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "message_type" to forward_type, "message_content" to forward_content, "message_other_type" to "forwarded", "send_time" to FieldValue.serverTimestamp(), "inside_reply" to inside_reply)
            firestore.collection("Group").document(group_id).collection("Chats").add(hashMap).addOnSuccessListener {
                // ********for empty inside reply message*********
                inside_reply.put("message_id", "")
                inside_reply.put("message_type", "")
                inside_reply.put("message_content", "")
                inside_reply.put("sender_id", "")
                inside_reply.put("position", "")
            }.addOnFailureListener {
                Log.d("FailureListener", "" + it.localizedMessage)
            }
        }
    }

    fun InitializeRecyclerview() {
        chatAdapter?.let {
            chat_recycler.layoutManager?.let {
                recyclerChatViewState = it.onSaveInstanceState()
            }
        }
        chat_recycler.setHasFixedSize(true)
        chat_recycler.removeAllViews()
        chat_recycler.layoutManager = LinearLayoutManager(this@GroupChatPage)
        chatAdapter = GroupChatAdapter(this@GroupChatPage, this@GroupChatPage, messagesArrayList)
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

    fun insideReply(position: Int) {
        helperMethods.showKeyboard(message)
        val groupMessageFireStore = messagesArrayList.get(position)
        inside_reply.put("message_id", groupMessageFireStore.message_id)
        inside_reply.put("message_type", groupMessageFireStore.message_type)
        inside_reply.put("message_content", groupMessageFireStore.message_content)
        inside_reply.put("sender_id", groupMessageFireStore.sender_id)
        inside_reply.put("position", position.toString())
        inside_reply_layout.visibility = View.VISIBLE
        if (groupMessageFireStore.sender_id.equals(dataUser.id)) {
            inside_reply_from.text = getString(R.string.you)
            inside_reply_from.setTextColor(ContextCompat.getColor(this@GroupChatPage, R.color.green))
        } else {
            inside_reply_from.text = findUserDetailsFromGroup(groupMessageFireStore.sender_id).fname + " " + findUserDetailsFromGroup(groupMessageFireStore.sender_id).lname
            inside_reply_from.setTextColor(ContextCompat.getColor(this@GroupChatPage, R.color.orange))
        }
        if (groupMessageFireStore.message_type.equals("text")) {
            inside_reply_text.text = groupMessageFireStore.message_content
            inside_reply_text.visibility = View.VISIBLE
            inside_reply_image_layout.visibility = View.GONE
        } else if (groupMessageFireStore.message_type.equals("image")) {
            Glide.with(this@GroupChatPage).load(groupMessageFireStore.message_content).apply(helperMethods.requestOption).into(inside_reply_image)
            inside_reply_image_layout.visibility = View.VISIBLE
            inside_reply_text.visibility = View.GONE
        }
    }

    fun firestoreLisiner() {
        firestore.collection("Group").document(group_id).get().addOnSuccessListener {
            groupItemData = it.toObject(GroupItem::class.java)!!
            groupItemData.group_id = it.id
            groupName.text = groupItemData.group_name
            groupMembers.text = "" + groupItemData.group_members.size + " " + getString(R.string.members)
            Glide.with(this@GroupChatPage).load(groupItemData.group_image).apply(helperMethods.profileRequestOption).into(groupIcon)

            firestore.collection("Users").whereIn("user_id", groupItemData.group_members).get().addOnSuccessListener {
                userArraylist.clear()
                for (data in it) {
                    val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                    userArraylist.add(dataUserFireStore)
                }
                chatFireStoreLisiner()
            }
        }
    }

    fun chatFireStoreLisiner() {
        firestoreRegistrar = firestore.collection("Group").document(group_id).collection("Chats").orderBy("send_time", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                messagesArrayList.clear()
                for (data in it) {
                    val groupMessageFireStore = data.toObject(GroupMessageFireStore::class.java)
                    groupMessageFireStore.message_id = data.id
                    messagesArrayList.add(groupMessageFireStore)
                }
                InitializeRecyclerview()
            }
        }
    }

    fun findUserDetailsFromGroup(user_id: String): DataUserFireStore {
        for (userDetails in userArraylist) {
            if (user_id.equals(userDetails.user_id)) {
                return userDetails
            }
        }
        return DataUserFireStore()
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        upload_image.setOnClickListener {
            helperMethods.ChangeProfilePhotoPopup(this@GroupChatPage)
        }
        send_msg.setOnClickListener {
            if (sendMessageValidation()) {
                val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "message_type" to "text", "message_content" to message.toText(), "message_other_type" to "normal", "send_time" to FieldValue.serverTimestamp(), "inside_reply" to inside_reply)
                firestore.collection("Group").document(group_id).collection("Chats").add(hashMap).addOnSuccessListener {
                    message.text = "".toEditable()
                    inside_reply_layout.visibility = View.GONE
                    inside_reply.put("message_id", "")
                    inside_reply.put("message_type", "")
                    inside_reply.put("message_content", "")
                    inside_reply.put("sender_id", "")
                    inside_reply.put("position", "")
                }.addOnFailureListener {
                    Log.d("FailureListener", "" + it.localizedMessage)
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
                    Toast.makeText(this@GroupChatPage, getString(R.string.could_not_get_image), Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this@GroupChatPage, getString(R.string.could_not_get_image), Toast.LENGTH_LONG).show()
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
                                val hashMap = hashMapOf<String, Any>("sender_id" to dataUser.id, "message_type" to "image", "message_content" to image_path, "message_other_type" to "normal", "send_time" to FieldValue.serverTimestamp(), "inside_reply" to inside_reply)
                                firestore.collection("Group").document(group_id).collection("Chats").add(hashMap).addOnSuccessListener {
                                    inside_reply_layout.visibility = View.GONE
                                    inside_reply.put("message_id", "")
                                    inside_reply.put("message_type", "")
                                    inside_reply.put("message_content", "")
                                    inside_reply.put("sender_id", "")
                                    inside_reply.put("position", "")
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

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}