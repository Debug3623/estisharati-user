package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import digital.upbeat.estisharati_user.Adapter.OnlineConsultationsAdapter
import digital.upbeat.estisharati_user.Adapter.RecentChatAdapter
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.DataUserFireStore
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_chat_home.*

class ChatHome : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    var onlineConsultationArraylist = arrayListOf<DataUserFireStore>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_home)
        initViews()
        clickEvents()
        InitializeRecyclerview()
        firestoreLisiner()
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

    fun firestoreLisiner() {
        firestore.collection("Users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                onlineConsultationArraylist = arrayListOf<DataUserFireStore>()
                for (data in querySnapshot) {
                    val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                    if (!dataUserFireStore.user_id.equals(dataUser.id)) {
                        onlineConsultationArraylist.add(dataUserFireStore)
                    }
                }
                InitializeRecyclerview()
            }
        }
    }

    fun InitializeRecyclerview() {
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Finance and Accounting")
        arrayList.add("Health and Fitness")
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Finance and Accounting")
        arrayList.add("Health and Fitness")
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Finance and Accounting")
        arrayList.add("Health and Fitness")
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Finance and Accounting")
        arrayList.add("Health and Fitness")

        if(!onlineConsultationArraylist.isEmpty()) {
            lebel_online_consultations.visibility=View.VISIBLE
            online_consultations_recycler.setHasFixedSize(true)
            online_consultations_recycler.removeAllViews()
            online_consultations_recycler.layoutManager = LinearLayoutManager(this@ChatHome, LinearLayoutManager.HORIZONTAL, false)
            online_consultations_recycler.adapter = OnlineConsultationsAdapter(this@ChatHome, this@ChatHome, onlineConsultationArraylist)
        }else{
            lebel_online_consultations.visibility=View.GONE
        }
        recent_chat_recycler.setHasFixedSize(true)
        recent_chat_recycler.removeAllViews()
        recent_chat_recycler.layoutManager = LinearLayoutManager(this@ChatHome)
        recent_chat_recycler.adapter = RecentChatAdapter(this@ChatHome, this@ChatHome, arrayList)
    }
}