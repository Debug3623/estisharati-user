package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.OnlineConsultationsAdapter
import digital.upbeat.estisharati_user.Adapter.RecentChatAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_chat_home.*

class ChatHome : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_home)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ChatHome)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
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

        online_consultations_recycler.setHasFixedSize(true)
        online_consultations_recycler.removeAllViews()
        online_consultations_recycler.layoutManager = LinearLayoutManager(this@ChatHome, LinearLayoutManager.HORIZONTAL, false)
        online_consultations_recycler.adapter = OnlineConsultationsAdapter(this@ChatHome, this@ChatHome, arrayList)

        recent_chat_recycler.setHasFixedSize(true)
        recent_chat_recycler.removeAllViews()
        recent_chat_recycler.layoutManager = LinearLayoutManager(this@ChatHome)
        recent_chat_recycler.adapter = RecentChatAdapter(this@ChatHome, this@ChatHome, arrayList)
    }
}