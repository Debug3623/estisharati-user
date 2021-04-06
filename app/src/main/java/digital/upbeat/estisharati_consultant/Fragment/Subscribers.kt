package digital.upbeat.estisharati_consultant.Fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import digital.upbeat.estisharati_consultant.Adapter.ChatGroupAdapter
import digital.upbeat.estisharati_consultant.Adapter.CreateGroupAdapter
import digital.upbeat.estisharati_consultant.Adapter.SubscribersAdapter
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.DataClassHelper.*
import digital.upbeat.estisharati_consultant.DataClassHelper.Calls.DataCallsFireStore
import digital.upbeat.estisharati_consultant.DataClassHelper.Chat.DataMessageFireStore
import digital.upbeat.estisharati_consultant.DataClassHelper.Group.GroupItem
import digital.upbeat.estisharati_consultant.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_consultant.DataClassHelper.MySubsribers.MySubscriberResponse
import digital.upbeat.estisharati_consultant.DataClassHelper.RecentChat.DataUserFireStore
import digital.upbeat.estisharati_consultant.DataClassHelper.RecentChat.DataUserMessageFireStore
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.UI.IncomingCall
import digital.upbeat.estisharati_consultant.Utils.alertActionClickListner
import kotlinx.android.synthetic.main.activity_chat_page.*
import kotlinx.android.synthetic.main.app_bar_consultant_drawer.*
import kotlinx.android.synthetic.main.create_group_layout.*
import kotlinx.android.synthetic.main.create_group_layout.view.*
import kotlinx.android.synthetic.main.fragment_subscribers.*
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
import kotlin.collections.ArrayList

class Subscribers : Fragment() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    var groupItemsArrayList: ArrayList<GroupItem> = arrayListOf()
    var userArraylist = arrayListOf<DataUserFireStore>()
    val dataUserMessageFireStoreArrayList = arrayListOf<DataUserMessageFireStore>()
    var UserListener: ListenerRegistration? = null
    var ChatListener1: ListenerRegistration? = null
    var ChatListener2: ListenerRegistration? = null
    var incomingCallListener: ListenerRegistration? = null
    private var subscribersAdapter: SubscribersAdapter? = null
    private var recyclerRecentChatViewState: Parcelable? = null
    lateinit var LayoutView: View
    var GroupImagePath: String? = null
    var groupMembers: ArrayList<String> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscribers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        clickEvents()
        mySubsribersApiCall()
    }

    fun initViews() {
        helperMethods = HelperMethods(requireContext())
        preferencesHelper = SharedPreferencesHelper(requireContext())
        firestore = FirebaseFirestore.getInstance()
        dataUser = preferencesHelper.logInConsultant
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
    }

    fun clickEvents() {
        forward_cancel.setOnClickListener {
            GlobalData.forwardType = ""
            GlobalData.forwardContent = ""
            forward_layout.visibility = View.GONE
            helperMethods.showToastMessage(getString(R.string.forward_cancel))
        }
        swipeRefresh.setOnRefreshListener {
            mySubsribersApiCall()
        }
        incomingCallListener = firestore.collection("Users").document(dataUser.id).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                val dataUserFireStore = documentSnapshot.toObject(DataUserFireStore::class.java)!!
                if (!dataUserFireStore.channel_unique_id.equals("")) {
                    firestore.collection("Calls").document(dataUserFireStore.channel_unique_id).get().addOnSuccessListener {
                        it.let {
                            val dataCallsFireStore = it.toObject(DataCallsFireStore::class.java)!!
                            if (dataCallsFireStore.receiver_id.equals(dataUser.id)) {
                                startActivity(Intent(requireContext(), IncomingCall::class.java))
                            } else {
                            }
                        }
                    }
                }
            }
        }
        groupShowHide.setOnClickListener {
            if (groupsRecycler.visibility == View.VISIBLE) {
                groupShowHide.rotation = 180F
                groupsRecycler.visibility = View.GONE
            } else {
                groupShowHide.rotation = 0F
                groupsRecycler.visibility = View.VISIBLE
            }
        }
        createGroupIcon.setOnClickListener {
            createGroupPopup("")
        }
    }

    fun onlineConsultationLisiner() {
        val userIds: ArrayList<String> = arrayListOf()
        for (User in GlobalData.mySubscriberResponse.data) {
            userIds.add(User.user_id)
        }
        ChatListener1 = firestore.collection("Chats").whereEqualTo("sender_id", dataUser.id).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            recentChatLisiner()
        }
        ChatListener2 = firestore.collection("Chats").whereEqualTo("receiver_id", dataUser.id).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            recentChatLisiner()
        }
        if (userIds.size > 0) {
            UserListener = firestore.collection("Users").whereIn("user_id", userIds).orderBy("fname", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                querySnapshot?.let {
                    userArraylist.clear()
                    for (data in querySnapshot) {
                        val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                        if (!dataUserFireStore.user_id.equals(dataUser.id)) {
                            userArraylist.add(dataUserFireStore)
                        }
                    }
                    recentChatLisiner()
                }
            }
        } else {
            recentChatRecycler()
        }
    }

    fun recentChatLisiner() {
        dataUserMessageFireStoreArrayList.clear()
        var userCount = 0
        for (data in userArraylist) {
            val IdArray = arrayListOf<Int>()
            IdArray.add(dataUser.id.toInt())
            IdArray.add(data.user_id.toInt())
            Collections.sort(IdArray)
            firestore.collection("Chats").whereEqualTo("communication_id", IdArray).orderBy("send_time", Query.Direction.ASCENDING).limitToLast(51).get().addOnSuccessListener {
                val messagesArrayList = arrayListOf<DataMessageFireStore>()
                it?.let {
                    for (dataMsg in it) {
                        val messageFireStore = dataMsg.toObject(DataMessageFireStore::class.java)
                        messagesArrayList.add(messageFireStore)
                        if (messageFireStore.receiver_id.equals(dataUser.id)) {
                            if (messageFireStore.message_status.equals("send")) {
                                val hashMap = hashMapOf<String, Any>("message_status" to "delivered")
                                firestore.collection("Chats").document(dataMsg.id).update(hashMap).addOnFailureListener {
                                    Log.d("FailureListener", "" + it.localizedMessage)
                                }
                            }
                        }
                    }
                }

                if (helperMethods.containsUserIdForChat(dataUserMessageFireStoreArrayList, data.user_id)) {
                    dataUserMessageFireStoreArrayList.add(DataUserMessageFireStore(data, messagesArrayList))
                }
                userCount++
                Log.d("FailureCheck", "" + userArraylist.size + "     " + userCount)
                if (userCount == userArraylist.size) {
                    recentChatRecycler()
                }
            }
        }

        firestore.collection("Group").whereEqualTo("creater_id", dataUser.id).addSnapshotListener { value, error ->
            value?.let {
                groupItemsArrayList.clear()
                for (groupItem in it) {
                    val groupItemData = groupItem.toObject(GroupItem::class.java)
                    groupItemData.group_id = groupItem.id
                    groupItemsArrayList.add(groupItemData)
                }
                groupChatList()
            }
        }
    }

    fun groupChatList() {
        totalGroupLabel.text = getString(R.string.your_groups) + " ( " + groupItemsArrayList.size + " )"
        groupsRecycler.setHasFixedSize(true)
        groupsRecycler.removeAllViews()
        groupsRecycler.adapter = ChatGroupAdapter(requireContext(), this@Subscribers, groupItemsArrayList)
        groupsRecycler.layoutManager = LinearLayoutManager(requireContext())
        if (groupItemsArrayList.size > 0) {
            groupLayout.visibility = View.VISIBLE
        } else {
            groupLayout.visibility = View.GONE
        }
    }

    fun recentChatRecycler() {
        if (shimmer_view_container.visibility == View.VISIBLE) {
            shimmer_view_container.stopShimmer()
            shimmer_view_container.visibility = View.GONE
            subscribersLayout.visibility = View.VISIBLE
        }
        sortArraylistDateWise()
        subscribersAdapter?.let {
            subscribers_recent_chat_recycler.layoutManager?.let {
                recyclerRecentChatViewState = it.onSaveInstanceState()
            }
        }
        subscribers_recent_chat_recycler.setHasFixedSize(true)
        subscribers_recent_chat_recycler.removeAllViews()
        subscribersAdapter = SubscribersAdapter(requireContext(), this@Subscribers, dataUserMessageFireStoreArrayList)
        subscribers_recent_chat_recycler.adapter = subscribersAdapter
        subscribers_recent_chat_recycler.layoutManager = LinearLayoutManager(requireContext())
        recyclerRecentChatViewState?.let {
            subscribers_recent_chat_recycler.layoutManager?.onRestoreInstanceState(recyclerRecentChatViewState)
        }
    }

    fun DeleteGroupPopup(groupId: String, groupName: String) {
        helperMethods.showAlertDialog(requireContext(), object : alertActionClickListner {
            override fun onActionOk() {
                firestore.collection("Group").document(groupId).delete().addOnSuccessListener {
                    helperMethods.showToastMessage(getString(R.string.group_deleted_successfully))
                }.addOnFailureListener {
                    Log.d("FailureListener", "" + it.localizedMessage)
                }
            }

            override fun onActionCancel() {
            }
        }, getString(R.string.delete_group), getString(R.string.are_you_sure_dou_yo_want_delete_this_group) + "\n " + groupName, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))
    }

    fun createGroupPopup(groupId: String) {
        Log.d("groupId", groupId)
        groupMembers.clear()
        GroupImagePath = null
        var groupItemData: GroupItem? = null
        val datauserFirestoreArrayList = arrayListOf<DataUserFireStore>()
        val userIds: ArrayList<String> = arrayListOf()
        for (User in GlobalData.mySubscriberResponse.data) {
            userIds.add(User.user_id)
        }


        LayoutView = LayoutInflater.from(context).inflate(R.layout.create_group_layout, null)
        val aleatdialog = AlertDialog.Builder(requireContext())
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(true)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        var createGroupAdapter: CreateGroupAdapter? = null
        LayoutView.groupUserRecycler.setHasFixedSize(true)
        LayoutView.groupUserRecycler.removeAllViews()
        LayoutView.groupUserRecycler.layoutManager = LinearLayoutManager(requireContext())

        if (!groupId.equals("")) {
            firestore.collection("Group").document(groupId).get().addOnSuccessListener {
                groupItemData = it.toObject(GroupItem::class.java)
                groupItemData?.let {
                    groupMembers = it.group_members
                    GroupImagePath = it.group_image
                    Glide.with(requireContext()).load(it.group_image).apply(helperMethods.requestOption).into(LayoutView.groupProfile)
                    LayoutView.groupName.text = it.group_name.toEditable()
                    LayoutView.activeStatus.isChecked = it.active
                    LayoutView.create.text = getString(R.string.update)
                    LayoutView.deleteGroup.visibility = View.VISIBLE

                    createGroupAdapter = CreateGroupAdapter(requireContext(), this@Subscribers, datauserFirestoreArrayList, groupMembers)
                    LayoutView.groupUserRecycler.adapter = createGroupAdapter
                }
            }
        } else {
            groupMembers.add(dataUser.id)
            LayoutView.create.text = getString(R.string.create)
            LayoutView.deleteGroup.visibility = View.GONE
        }


        if (userIds.size > 0) {
            firestore.collection("Users").whereIn("user_id", userIds).orderBy("fname", Query.Direction.ASCENDING).get().addOnSuccessListener {
                it?.let {
                    for (data in it) {
                        val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                        if (!dataUserFireStore.user_id.equals(dataUser.id)) {
                            datauserFirestoreArrayList.add(dataUserFireStore)
                        }
                    }
                    createGroupAdapter = CreateGroupAdapter(requireContext(), this@Subscribers, datauserFirestoreArrayList, groupMembers)
                    LayoutView.groupUserRecycler.adapter = createGroupAdapter
                }
            }
        }
        LayoutView.groupProfile.setOnClickListener {
            helperMethods.ChangeProfilePhotoPopup(requireActivity())
        }
        LayoutView.close.setOnClickListener {
            dialog.dismiss()
        }
        LayoutView.deleteGroup.setOnClickListener {
            dialog.dismiss()
            DeleteGroupPopup(groupId, groupItemData!!.group_name)
        }
        LayoutView.create.setOnClickListener {
            if (LayoutView.groupName.text.toString().equals("")) {
                helperMethods.showToastMessage(getString(R.string.enter_group_name))
                return@setOnClickListener
            }
            if (GroupImagePath == null) {
                helperMethods.showToastMessage(getString(R.string.upload_group_profile_picture))
                return@setOnClickListener
            }
            if (groupMembers.size == 1) {
                helperMethods.showToastMessage(getString(R.string.select_group_members))
                return@setOnClickListener
            }
            if (!helperMethods.isConnectingToInternet) {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                return@setOnClickListener
            }
            if (groupId.equals("")) {
                val hashMap = hashMapOf<String, Any>("creater_id" to dataUser.id, "group_name" to LayoutView.groupName.text.toString(), "group_image" to GroupImagePath!!, "group_members" to groupMembers, "active" to LayoutView.activeStatus.isChecked, "time" to FieldValue.serverTimestamp())
                firestore.collection("Group").add(hashMap).addOnSuccessListener {
                    helperMethods.showToastMessage(getString(R.string.group_created_successfully))
                }.addOnFailureListener {
                    Log.d("FailureListener", "" + it.localizedMessage)
                }
            } else {
                val hashMap = hashMapOf<String, Any>("creater_id" to dataUser.id, "group_name" to LayoutView.groupName.text.toString(), "group_image" to GroupImagePath!!, "group_members" to groupMembers, "active" to LayoutView.activeStatus.isChecked, "time" to FieldValue.serverTimestamp())
                firestore.collection("Group").document(groupId).set(hashMap).addOnSuccessListener {
                    helperMethods.showToastMessage(getString(R.string.group_updated_successfully))
                }.addOnFailureListener {
                    Log.d("FailureListener", "" + it.localizedMessage)
                }
            }
            dialog.dismiss()
        }
    }

    fun sortArraylistDateWise() {
        Collections.sort(dataUserMessageFireStoreArrayList, object : Comparator<DataUserMessageFireStore> {
            override fun compare(
                dataUserMessageFireStore1: DataUserMessageFireStore,
                dataUserMessageFireStore2: DataUserMessageFireStore,
            ): Int {
                if (dataUserMessageFireStore1.messagesArrayList.size > 0 && dataUserMessageFireStore2.messagesArrayList.size > 0) {
                    if (dataUserMessageFireStore1.messagesArrayList.get(dataUserMessageFireStore1.messagesArrayList.lastIndex).send_time == null || dataUserMessageFireStore2.messagesArrayList.get(dataUserMessageFireStore2.messagesArrayList.lastIndex).send_time == null) {
                        return 0
                    } else {
                        return dataUserMessageFireStore1.messagesArrayList.get(dataUserMessageFireStore1.messagesArrayList.lastIndex).send_time.compareTo(dataUserMessageFireStore2.messagesArrayList.get(dataUserMessageFireStore2.messagesArrayList.lastIndex).send_time)
                    }
                } else {
                    return 0
                }
            }
        })
        Collections.reverse(dataUserMessageFireStoreArrayList)
    }

    override fun onStart() {
        super.onStart()
        if (!GlobalData.forwardContent.isEmpty()) {
            forward_layout.visibility = View.VISIBLE
        }
        if (GlobalData.isInitialized()) {
            requireActivity().notificationCount.text = GlobalData.mySubscriberResponse.notification_count
        }
    }

    override fun onStop() {
        super.onStop()
        forward_layout.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        UserListener?.remove()
        ChatListener1?.remove()
        ChatListener2?.remove()
    }

    fun getImageUrlForChatApiCall(filePath: String) {
        val file = File(filePath)
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.getName(), requestBody)

        helperMethods.showProgressDialog(getString(R.string.image_uploading))
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
                                Glide.with(requireContext()).load(image_path).apply(helperMethods.requestOption).into(LayoutView.groupProfile)
                                GroupImagePath = image_path
                            } else {
                                val message = jsonObject.getString("message")
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    requireActivity().finish()
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

    fun mySubsribersApiCall() {
        Log.d("BaseURL",GlobalData.BaseUrl)
        shimmer_view_container.startShimmer()
        shimmer_view_container.visibility = View.VISIBLE
        subscribersLayout.visibility = View.GONE
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.MY_SUBSRIBERS_API_CALL("Bearer ${dataUser.access_token}", GlobalData.FcmToken)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            GlobalData.mySubscriberResponse = Gson().fromJson(response.body()!!.string(), MySubscriberResponse::class.java)
                            if (GlobalData.mySubscriberResponse.status.equals("200")) {
                                onlineConsultationLisiner()
                                requireActivity().notificationCount.text = GlobalData.mySubscriberResponse.notification_count
                            } else {
                                if (helperMethods.checkTokenValidation(GlobalData.mySubscriberResponse.status, GlobalData.mySubscriberResponse.message)) {
                                    requireActivity().finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), GlobalData.mySubscriberResponse.message)
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
                swipeRefresh.isRefreshing = false
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}