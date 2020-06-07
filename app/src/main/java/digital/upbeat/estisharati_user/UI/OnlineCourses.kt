package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
        initializeRecyclerview()
        initializeSpinner()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@OnlineCourses)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun initializeRecyclerview() {
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("All Courses")
        arrayList.add("Graphic Designing")
        arrayList.add("Big Data Analysis")
        arrayList.add("Online MBA")
        arrayList.add("Social Media Marketing")
        arrayList.add("Programming")
        arrayList.add("App Development")
        arrayList.add("Film Making")

        online_courses_recycler.setHasFixedSize(true)
        online_courses_recycler.removeAllViews()
        online_courses_recycler.layoutManager = LinearLayoutManager(this@OnlineCourses)
        online_courses_recycler.adapter = OnlineCoursesAdapter(this@OnlineCourses, this@OnlineCourses, arrayList)
    }

    fun initializeSpinner() {
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("All Courses")
        arrayList.add("Graphic Designing")
        arrayList.add("Big Data Analysis")
        arrayList.add("Online MBA")
        arrayList.add("Social Media Marketing")
        arrayList.add("Programming")
        arrayList.add("App Development")
        arrayList.add("Film Making")
        val typeface = ResourcesCompat.getFont(this@OnlineCourses, R.font.almarai_regular)
        val adapter = ArrayAdapter(this@OnlineCourses, R.layout.support_simple_spinner_dropdown_item, arrayList)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        courses_spinner.adapter = adapter
        courses_spinner.setSelection(0, true)
        val v2 = courses_spinner.selectedView
        (v2 as TextView).textSize = 15f
        v2.typeface = typeface
        v2.setTextColor(ContextCompat.getColor(this@OnlineCourses, R.color.white))
        courses_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                (view as TextView).textSize = 15f
                view.typeface = typeface
                view.setTextColor(ContextCompat.getColor(this@OnlineCourses, R.color.white))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}
