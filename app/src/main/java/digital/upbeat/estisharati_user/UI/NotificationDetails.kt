package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.ConsultantCommentsReplySubAdapter
import digital.upbeat.estisharati_user.DataClassHelper.DataTextsAndColors
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_notification_details.*
import kotlinx.android.synthetic.main.activity_notifications.nav_back

class NotificationDetails : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_details)
        initViews()
        clickEvents()
        setNotificationDetails()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@NotificationDetails)
    }

    fun setNotificationDetails() {
        val textColorsArrayList = arrayListOf<DataTextsAndColors>()
        textColorsArrayList.add(DataTextsAndColors("Taliha Al-Jabar ", ContextCompat.getColor(this@NotificationDetails, R.color.black)))
        textColorsArrayList.add(DataTextsAndColors("reply in ", ContextCompat.getColor(this@NotificationDetails, R.color.gray)))
        textColorsArrayList.add(DataTextsAndColors("Introduction to social media Course", ContextCompat.getColor(this@NotificationDetails, R.color.black)))
        message.text = helperMethods.getColorStringFromArray(textColorsArrayList)
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
        notification_sub_recycler.adapter = ConsultantCommentsReplySubAdapter(this@NotificationDetails, null, null, arrayListOf(), arrayListOf())
    }
}