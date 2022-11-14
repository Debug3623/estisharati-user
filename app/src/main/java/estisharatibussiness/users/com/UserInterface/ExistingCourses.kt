package estisharatibussiness.users.com.UserInterface

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import estisharatibussiness.users.com.Adapter.ExistingCoursesAdapter
import estisharatibussiness.users.com.DataClassHelper.Packages.Course
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_existing_courses.*

class ExistingCourses : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_existing_courses)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ExistingCourses)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun InitializeRecyclerview() {
        val coursesArrayList = intent.getParcelableArrayListExtra<Course>("courses") as ArrayList<Course>
        existing_courses_recycler.setHasFixedSize(true)
        existing_courses_recycler.removeAllViews()
        existing_courses_recycler.layoutManager = GridLayoutManager(this@ExistingCourses, 2, GridLayoutManager.VERTICAL, false)
        existing_courses_recycler.adapter = ExistingCoursesAdapter(this@ExistingCourses, this@ExistingCourses, coursesArrayList)
    }
}