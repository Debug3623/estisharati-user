package digital.upbeat.estisharati_user.UI

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.MyCoursesAdapter
import digital.upbeat.estisharati_user.Adapter.NotificationsAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
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
    fun changeTextColor( textView: TextView,color: Color){
        val text = "<font color='#000000'></font>"; //set Black color of name
        /* check API version, according to version call method of Html class  */
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            textView.text= Html.fromHtml(text)
        } else {
            textView.text=Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)   //append text into textView
        }
    }
}