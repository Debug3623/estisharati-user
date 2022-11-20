package estisharatibussiness.users.com.FragmentClasses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import estisharatibussiness.users.com.AdapterClasses.CourseChapterAdapter
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityCourseResource
import kotlinx.android.synthetic.main.fragment_course_documents.*

class CourseDocuments(val activityCourseResource: ActivityCourseResource) : Fragment() {
    lateinit var helperMethods: HelperMethods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_documents, container, false)
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
        courseDocumentRecycler.setHasFixedSize(true)
        courseDocumentRecycler.removeAllViews()
        courseDocumentRecycler.layoutManager = LinearLayoutManager(requireContext())
        courseDocumentRecycler.adapter = CourseChapterAdapter(requireContext(), null, this@CourseDocuments, arrayListOf(), activityCourseResource.startCourseResponse.data.documents)
        if (activityCourseResource.startCourseResponse.data.documents.size > 0) {
            courseDocumentRecycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            courseDocumentRecycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }
}