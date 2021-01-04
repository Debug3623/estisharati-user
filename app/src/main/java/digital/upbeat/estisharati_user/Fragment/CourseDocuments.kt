package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.CourseChapterAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseResource
import kotlinx.android.synthetic.main.fragment_course_documents.*

class CourseDocuments(val courseResource: CourseResource) : Fragment() {
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
        courseDocumentRecycler.adapter = CourseChapterAdapter(requireContext(), null, this@CourseDocuments, arrayListOf(), courseResource.startCourseResponse.data.documents)
        if (courseResource.startCourseResponse.data.documents.size > 0) {
            courseDocumentRecycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            courseDocumentRecycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }
}