package digital.upbeat.estisharati_consultant.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_consultant.Adapter.NotificationsAdapter
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.R
import kotlinx.android.synthetic.main.activity_notifications.*

class Notifications : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Notifications)
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

        notifications_recycler.setHasFixedSize(true)
        notifications_recycler.removeAllViews()
        notifications_recycler.layoutManager = LinearLayoutManager(this@Notifications)
        notifications_recycler.adapter = NotificationsAdapter(this@Notifications, this@Notifications, arrayList)
    }
}