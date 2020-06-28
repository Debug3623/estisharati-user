package digital.upbeat.estisharati_user.UI

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import digital.upbeat.estisharati_user.Adapter.ChatAdapter
import digital.upbeat.estisharati_user.Adapter.OnlineConsultationsAdapter
import digital.upbeat.estisharati_user.Adapter.RecentChatAdapter
import digital.upbeat.estisharati_user.DataClassHelper.DataMessageFireStore
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.DataUserFireStore
import digital.upbeat.estisharati_user.DataClassHelper.DataUserMessageFireStore
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_chat_home.*
import java.util.*

class ChatHome : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    var onlineConsultationArraylist = arrayListOf<DataUserFireStore>()
    var userArraylist = arrayListOf<DataUserFireStore>()
    val dataUserMessageFireStoreArrayList = arrayListOf<DataUserMessageFireStore>()
    var UserListener: ListenerRegistration? = null
    var ChatListener: ListenerRegistration? = null
    private var recentChatAdapter: RecentChatAdapter? = null
    private var recyclerRecentChatViewState: Parcelable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_home)
        initViews()
        clickEvents()
        onlineConsultationLisiner()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ChatHome)
        preferencesHelper = SharedPreferencesHelper(this@ChatHome)
        firestore = FirebaseFirestore.getInstance()
        dataUser = preferencesHelper.getLogInUser()
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun onlineConsultationLisiner() {
        UserListener = firestore.collection("Users").orderBy("fname", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                onlineConsultationArraylist.clear()
                userArraylist.clear()
                for (data in querySnapshot) {
                    val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                    if (!dataUserFireStore.user_id.equals(dataUser.id)) {
                        onlineConsultationArraylist.add(dataUserFireStore)
                        userArraylist.add(dataUserFireStore)
                    }
                }
                recentChatLisiner()
                onlineConsultationsRecycler()
            }
        }
    }

    fun recentChatLisiner() {
        ChatListener = firestore.collection("Chats").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            dataUserMessageFireStoreArrayList.clear()
            for (data in userArraylist) {
                val IdArray = arrayListOf<Int>()
                IdArray.add(dataUser.id.toInt())
                IdArray.add(data.user_id.toInt())
                Collections.sort(IdArray)
                firestore.collection("Chats").whereEqualTo("communication_id", IdArray).orderBy("send_time", Query.Direction.ASCENDING).get().addOnSuccessListener {
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
                    if (!messagesArrayList.isEmpty() && !dataUserMessageFireStoreArrayList.contains(DataUserMessageFireStore(data, messagesArrayList))) {
                        dataUserMessageFireStoreArrayList.add(DataUserMessageFireStore(data, messagesArrayList))
                        recentChatRecycler()
                    }
                }
            }
        }
    }

    fun onlineConsultationsRecycler() {
        if (!onlineConsultationArraylist.isEmpty()) {
            lebel_online_consultations.visibility = View.VISIBLE
            online_consultations_recycler.setHasFixedSize(true)
            online_consultations_recycler.removeAllViews()
            online_consultations_recycler.layoutManager = LinearLayoutManager(this@ChatHome, LinearLayoutManager.HORIZONTAL, false)
            online_consultations_recycler.adapter = OnlineConsultationsAdapter(this@ChatHome, this@ChatHome, onlineConsultationArraylist)
        } else {
            lebel_online_consultations.visibility = View.GONE
        }
    }

    fun recentChatRecycler() {
        sortArraylistDateWise()
        recentChatAdapter.let {
            recent_chat_recycler.layoutManager?.let {
                recyclerRecentChatViewState = it.onSaveInstanceState()
            }
        }
        recent_chat_recycler.setHasFixedSize(true)
        recent_chat_recycler.removeAllViews()
        recentChatAdapter = RecentChatAdapter(this@ChatHome, this@ChatHome, dataUserMessageFireStoreArrayList)
        recent_chat_recycler.adapter = recentChatAdapter
        recent_chat_recycler.layoutManager = LinearLayoutManager(this@ChatHome)
        recyclerRecentChatViewState?.let {
            recent_chat_recycler.layoutManager?.onRestoreInstanceState(recyclerRecentChatViewState)
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

    override fun onDestroy() {
        super.onDestroy()
        UserListener?.remove()
        ChatListener?.remove()
    }
}