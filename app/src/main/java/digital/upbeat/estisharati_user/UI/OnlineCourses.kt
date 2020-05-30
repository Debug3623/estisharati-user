package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.OnlineCoursesAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_online_courses.*

class OnlineCourses : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_courses)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@OnlineCourses)
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

        online_courses_recycler.setHasFixedSize(true)
        online_courses_recycler.removeAllViews()
        online_courses_recycler.layoutManager = LinearLayoutManager(this@OnlineCourses)
        online_courses_recycler.adapter = OnlineCoursesAdapter(this@OnlineCourses, this@OnlineCourses, arrayList)
    }
}
