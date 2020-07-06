package digital.upbeat.estisharati_consultant.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_consultant.Adapter.NotificationsSubAdapter
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.R
import kotlinx.android.synthetic.main.activity_notification_details.*
import kotlinx.android.synthetic.main.activity_notifications.nav_back

class NotificationDetails : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_details)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@NotificationDetails)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        main_item.setOnClickListener {
            sub_item_reply_layout.visibility = if (sub_item_reply_layout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    fun InitializeRecyclerview() {
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")


        notification_sub_recycler.setHasFixedSize(true)
        notification_sub_recycler.removeAllViews()
        notification_sub_recycler.layoutManager = LinearLayoutManager(this@NotificationDetails)
        notification_sub_recycler.adapter = NotificationsSubAdapter(this@NotificationDetails, this@NotificationDetails, arrayList)
    }
}