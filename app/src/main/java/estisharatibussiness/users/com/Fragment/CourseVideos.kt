package estisharatibussiness.users.com.Fragment

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import estisharatibussiness.users.com.Adapter.CourseChapterAdapter
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.ActivityCourseResource
import kotlinx.android.synthetic.main.fragment_course_videos.*

class CourseVideos(val activityCourseResource: ActivityCourseResource) : Fragment() {
    private var courseChapterAdapter: CourseChapterAdapter? = null
    private var recyclerViewState: Parcelable? = null
    lateinit var helperMethods: HelperMethods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_videos, container, false)
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
        courseChapterAdapter?.let {
            courseVideoRecycler.layoutManager?.let {
                recyclerViewState = it.onSaveInstanceState()
            }
        }
        courseVideoRecycler.setHasFixedSize(true)
        courseVideoRecycler.removeAllViews()
        courseVideoRecycler.layoutManager = LinearLayoutManager(requireContext())
        courseChapterAdapter = CourseChapterAdapter(requireContext(), this@CourseVideos, null, activityCourseResource.startCourseResponse.data.videos, arrayListOf())
        courseVideoRecycler.adapter = courseChapterAdapter
        recyclerViewState?.let {
            courseVideoRecycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }

        if (activityCourseResource.startCourseResponse.data.videos.size > 0) {
            courseVideoRecycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            courseVideoRecycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }
}