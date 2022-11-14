package estisharatibussiness.users.com.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import estisharatibussiness.users.com.Adapter.InstructorAdapter
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.ActivityCourseDetails
import kotlinx.android.synthetic.main.fragment_instructor.*

class Instructor(val activityCourseDetails: ActivityCourseDetails) : Fragment() {
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
        indicator_recycler.adapter = InstructorAdapter(requireContext(), this@Instructor, activityCourseDetails.responseCoursesDetails.consultants)
        if (activityCourseDetails.responseCoursesDetails.consultants.size > 0) {
            indicator_recycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            indicator_recycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }
}