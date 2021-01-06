package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.ConsultantCommentsReplyAdapter
import digital.upbeat.estisharati_user.Adapter.InstructorAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseDetails
import kotlinx.android.synthetic.main.fragment_comments.*
import kotlinx.android.synthetic.main.fragment_instructor.*

class Instructor(val courseDetails: CourseDetails) : Fragment() {
    lateinit var helperMethods: HelperMethods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instructor, container, false)
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
        indicator_recycler.setHasFixedSize(true)
        indicator_recycler.removeAllViews()
        indicator_recycler.layoutManager = LinearLayoutManager(requireContext())
        indicator_recycler.adapter = InstructorAdapter(requireContext(), this@Instructor, courseDetails.responseCoursesDetails.consultants)
        if (courseDetails.responseCoursesDetails.consultants.size > 0) {
            indicator_recycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            indicator_recycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }
}