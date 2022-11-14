package estisharatibussiness.users.com.UserInterface

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import estisharatibussiness.users.com.Adapter.ChatGroupAdapter
import estisharatibussiness.users.com.Adapter.OnlineConsultationsAdapter
import estisharatibussiness.users.com.Adapter.RecentChatAdapter
import estisharatibussiness.users.com.Adapter.SearchUserAdapter
import estisharatibussiness.users.com.DataClassHelper.Chat.DataMessageFireStore
import estisharatibussiness.users.com.DataClassHelper.Login.DataUser
import estisharatibussiness.users.com.DataClassHelper.Chat.DataUserFireStore
import estisharatibussiness.users.com.DataClassHelper.Chat.DataUserMessageFireStore
import estisharatibussiness.users.com.DataClassHelper.Group.GroupItem
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_chat_home.*
import java.util.*

class ChatHomeActivity : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    var onlineConsultationArraylist = arrayListOf<DataUserFireStore>()
    var userArraylist = arrayListOf<DataUserFireStore>()
    val dataUserMessageFireStoreArrayList = arrayListOf<DataUserMessageFireStore>()
    var groupItemsArrayList: ArrayList<GroupItem> = arrayListOf()
    var UserListener: ListenerRegistration? = null
    var consultantListener: ListenerRegistration? = null
    var ChatListener1: ListenerRegistration? = null
    var ChatListener2: ListenerRegistration? = null
    private var recentChatAdapter: RecentChatAdapter? = null
    private var recyclerRecentChatViewState: Parcelable? = null
    var searchUserdialog: AlertDialog? = null
    val consultantIdArrayList: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_home)
        initViews()
        clickEvents()
        onlineConsultationLisiner()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ChatHomeActivity)
        preferencesHelper = SharedPreferencesHelper(this@ChatHomeActivity)
        firestore = FirebaseFirestore.getInstance()
        dataUser = preferencesHelper.logInUser
        if (GlobalData.isThingInitialized()) {
            notificationCount.text = GlobalData.homeResponse.notification_count
        }
        for (consultant in GlobalData.homeResponse.consultants) {
            consultantIdArrayList.add(consultant.id)
        }
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        forward_cancel.setOnClickListener {
            GlobalData.forwardType = ""
            GlobalData.forwardContent = ""
            forward_layout.visibility = View.GONE
            action_bar_title.text = getString(R.string.chat)
            helperMethods.showToastMessage(getString(R.string.forward_cancel))
        }
        search_user_icon.setOnClickListener {
            searchUserPopup()
        }
        notificationLayout.setOnClickListener {
            startActivity(Intent(this@ChatHomeActivity, Notifications::class.java))
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
    }

    fun onlineConsultationLisiner() {
        shimmer_view_container.startShimmer()
        shimmer_view_container.visibility = View.VISIBLE
        recent_chat_recycler.visibility = View.GONE
        ChatListener1 = firestore.collection("Chats").whereEqualTo("sender_id", dataUser.id).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            recentChatLisiner()
        }
        ChatListener2 = firestore.collection("Chats").whereEqualTo("receiver_id", dataUser.id).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            recentChatLisiner()
        }
        UserListener = firestore.collection("Users").orderBy("fname", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                userArraylist.clear()
                for (data in it) {
                    val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                    if (!dataUserFireStore.user_id.equals(dataUser.id)) {
                        userArraylist.add(dataUserFireStore)
                    }
                }
                recentChatLisiner()
            }
        }

        Log.d("consultantIdArrayList", consultantIdArrayList.toString())
//        if (GlobalData.homeResponse.consultants.size > 0) {
            consultantListener = firestore.collection("Users").orderBy("fname", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                querySnapshot?.let {
                    onlineConsultationArraylist = arrayListOf<DataUserFireStore>()
                    for (data in querySnapshot) {
                        val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                        if (!dataUserFireStore.user_id.equals(dataUser.id)) {
                            if (dataUserFireStore.user_type.equals("user") || helperMethods.findConsultantId(dataUserFireStore.user_id)) {
                                onlineConsultationArraylist.add(dataUserFireStore)
                            }
                        }
                    }
                    onlineConsultationsRecycler()
                }
            }
//        }
        firestore.collection("Group").addSnapshotListener { value, error ->
            value?.let {
                groupItemsArrayList.clear()
                for (groupItem in it) {
                    val groupItemData = groupItem.toObject(GroupItem::class.java)
                    if (groupItemData.group_members.contains(dataUser.id) && groupItemData.active) {
                        groupItemData.group_id = groupItem.id
                        groupItemsArrayList.add(groupItemData)
                    }
                }
                groupChatList()
            }
        }
    }

    fun groupChatList() {
        totalGroupLabel.text = getString(R.string.your_groups) + " ( " + groupItemsArrayList.size + " )"
        groupsRecycler.setHasFixedSize(true)
        groupsRecycler.removeAllViews()
        groupsRecycler.adapter = ChatGroupAdapter(this@ChatHomeActivity, this@ChatHomeActivity, groupItemsArrayList)
        groupsRecycler.layoutManager = LinearLayoutManager(this@ChatHomeActivity)
        if (groupItemsArrayList.size > 0) {
            groupLayout.visibility = View.VISIBLE
        } else {
            groupLayout.visibility = View.GONE
        }
    }

    fun recentChatLisiner() {
        dataUserMessageFireStoreArrayList.clear()
        var userMsgFetchCount = 0
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
                        messageFireStore.message_id = dataMsg.id
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

                if (!messagesArrayList.isEmpty() && helperMethods.containsUserIdForChat(dataUserMessageFireStoreArrayList, data.user_id)) {
                    dataUserMessageFireStoreArrayList.add(DataUserMessageFireStore(data, messagesArrayList))
                }
                userMsgFetchCount++
                Log.d("FailureCheck", "" + userArraylist.size + "     " + userMsgFetchCount)
                if (userMsgFetchCount == userArraylist.size) {
                    recentChatRecycler()
                }
            }
        }
    }

    fun onlineConsultationsRecycler() {
        if (!onlineConsultationArraylist.isEmpty()) {
            lebel_online_consultations.visibility = View.VISIBLE
            online_consultations_recycler.setHasFixedSize(true)
            online_consultations_recycler.removeAllViews()
            online_consultations_recycler.layoutManager = LinearLayoutManager(this@ChatHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            online_consultations_recycler.adapter = OnlineConsultationsAdapter(this@ChatHomeActivity, this@ChatHomeActivity, onlineConsultationArraylist)
        } else {
            lebel_online_consultations.visibility = View.GONE
        }
    }

    fun recentChatRecycler() {
        if (shimmer_view_container.visibility == View.VISIBLE) {
            shimmer_view_container.stopShimmer()
            shimmer_view_container.visibility = View.GONE
            recent_chat_recycler.visibility = View.VISIBLE
        }
        sortArraylistDateWise()
        recentChatAdapter?.let {
            recent_chat_recycler.layoutManager?.let {
                recyclerRecentChatViewState = it.onSaveInstanceState()
            }
        }
        recent_chat_recycler.setHasFixedSize(true)
        recent_chat_recycler.removeAllViews()
        recentChatAdapter = RecentChatAdapter(this@ChatHomeActivity, this@ChatHomeActivity, dataUserMessageFireStoreArrayList)
        recent_chat_recycler.adapter = recentChatAdapter
        recent_chat_recycler.layoutManager = LinearLayoutManager(this@ChatHomeActivity)
        recyclerRecentChatViewState?.let {
            recent_chat_recycler.layoutManager?.onRestoreInstanceState(recyclerRecentChatViewState)
        }
        if (dataUserMessageFireStoreArrayList.size > 0) {
            recent_chat_recycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            recent_chat_recycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }

    fun sortArraylistDateWise() {
        Collections.sort(dataUserMessageFireStoreArrayList, object : Comparator<DataUserMessageFireStore> {
            override fun compare(dataUserMessageFireStore1: DataUserMessageFireStore, dataUserMessageFireStore2: DataUserMessageFireStore): Int {
                if (dataUserMessageFireStore1.messagesArrayList.get(dataUserMessageFireStore1.messagesArrayList.lastIndex).send_time == null || dataUserMessageFireStore2.messagesArrayList.get(dataUserMessageFireStore2.messagesArrayList.lastIndex).send_time == null) {
                    return 0
                } else {
                    return dataUserMessageFireStore1.messagesArrayList.get(dataUserMessageFireStore1.messagesArrayList.lastIndex).send_time.compareTo(dataUserMessageFireStore2.messagesArrayList.get(dataUserMessageFireStore2.messagesArrayList.lastIndex).send_time)
                }
            }
        })
        Collections.reverse(dataUserMessageFireStoreArrayList)
    }

    fun searchUserPopup() {
        val LayoutView = LayoutInflater.from(this@ChatHomeActivity).inflate(R.layout.search_user_layout, null)
        val alertDialog = AlertDialog.Builder(this@ChatHomeActivity)
        alertDialog.setView(LayoutView)
        alertDialog.setCancelable(false)
        searchUserdialog = alertDialog.create()
        searchUserdialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val search_text = LayoutView.findViewById<EditText>(R.id.search_text)
        val search_close_btn = LayoutView.findViewById<ImageView>(R.id.search_close_btn)
        val search_not_found = LayoutView.findViewById<LinearLayout>(R.id.search_not_found)
        val search_user_recyclerview = LayoutView.findViewById<RecyclerView>(R.id.search_user_recyclerview)
        search_user_recyclerview.setHasFixedSize(true)
        search_user_recyclerview.removeAllViews()
        search_user_recyclerview.layoutManager = LinearLayoutManager(this@ChatHomeActivity)
        var searchUserAdapter: SearchUserAdapter? = null
        val datauserFirestoreArrayList = arrayListOf<DataUserFireStore>()
        var dataPassed = arrayListOf<DataUserFireStore>()
        if (GlobalData.homeResponse.consultants.size > 0) {
            firestore.collection("Users").orderBy("fname", Query.Direction.ASCENDING).get().addOnSuccessListener {
                it?.let {
                    for (data in it) {
                        val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                        if (!dataUserFireStore.user_id.equals(dataUser.id)) {
                            if (dataUserFireStore.user_type.equals("user") || helperMethods.findConsultantId(dataUserFireStore.user_id)) {
                                datauserFirestoreArrayList.add(dataUserFireStore)
                            }
                        }
                    }
                    searchUserdialog?.show()
                    dataPassed.addAll(datauserFirestoreArrayList)
                    if (dataPassed.size > 0) {
                        search_user_recyclerview.visibility = View.VISIBLE
                        search_not_found.visibility = View.GONE
                        searchUserAdapter = SearchUserAdapter(this@ChatHomeActivity, this@ChatHomeActivity, dataPassed)
                        search_user_recyclerview.adapter = searchUserAdapter
                    } else {
                        search_user_recyclerview.visibility = View.GONE
                        search_not_found.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            search_user_recyclerview.visibility = View.GONE
            search_not_found.visibility = View.VISIBLE
        }
        search_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                dataPassed.clear()
                if (!s.toString().equals("")) {
                    for (data in datauserFirestoreArrayList) {
                        val check = data.fname + " " + data.lname
                        if (check.contains(s.toString(), true)) {
                            dataPassed.add(data)
                        }
                    }
                } else {
                    dataPassed.addAll(datauserFirestoreArrayList)
                }
                if (dataPassed.size > 0) {
                    search_user_recyclerview.visibility = View.VISIBLE
                    search_not_found.visibility = View.GONE
                    searchUserAdapter = SearchUserAdapter(this@ChatHomeActivity, this@ChatHomeActivity, dataPassed)
                    search_user_recyclerview.adapter = searchUserAdapter
                } else {
                    search_user_recyclerview.visibility = View.GONE
                    search_not_found.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        search_close_btn.setOnClickListener { searchUserdialog?.dismiss() }
    }

    override fun onStart() {
        super.onStart()
        if (!GlobalData.forwardContent.isEmpty()) {
            action_bar_title.text = getString(R.string.forward_to)
            forward_layout.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        action_bar_title.text = getString(R.string.chat)
        GlobalData.forwardType = ""
        GlobalData.forwardContent = ""
        forward_layout.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        UserListener?.remove()
        ChatListener1?.remove()
        ChatListener2?.remove()
        consultantListener?.remove()
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}