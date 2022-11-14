package estisharatibussiness.users.com.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import estisharatibussiness.users.com.Adapter.CourseContentAdapter
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.ActivityCourseDetails
import kotlinx.android.synthetic.main.fragment_course_content.*

class CourseContent(val activityCourseDetails: ActivityCourseDetails) : Fragment() {
    lateinit var helperMethods: HelperMethods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(requireContext())
    }

    fun clickEvents() {
    }

    fun InitializeRecyclerview() {
        courseContentRecycler.setHasFixedSize(true)
        courseContentRecycler.removeAllViews()
        courseContentRecycler.layoutManager = LinearLayoutManager(requireContext())
        courseContentRecycler.adapter = CourseContentAdapter(requireContext(), this@CourseContent, activityCourseDetails.responseCoursesDetails.course_resources)
        if (activityCourseDetails.responseCoursesDetails.course_resources.size > 0) {
            courseContentRecycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            courseContentRecycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }
}