package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.CourseContentAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseDetails
import kotlinx.android.synthetic.main.fragment_course_content.*

class CourseContent(val courseDetails: CourseDetails) : Fragment() {
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
        courseContentRecycler.adapter = CourseContentAdapter(requireContext(), this@CourseContent, courseDetails.responseCoursesDetails.course_resources)
        if (courseDetails.responseCoursesDetails.course_resources.size > 0) {
            courseContentRecycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            courseContentRecycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }
}