package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.ChatAdapter
import digital.upbeat.estisharati_user.Adapter.CourseChapterAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseResource
import kotlinx.android.synthetic.main.activity_chat_page.*
import kotlinx.android.synthetic.main.fragment_course_content.*

class CourseVideos(val courseResource: CourseResource) : Fragment() {
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
        courseChapterAdapter = CourseChapterAdapter(requireContext(), this@CourseVideos, null, courseResource.startCourseResponse.data.videos, arrayListOf())
        courseVideoRecycler.adapter = courseChapterAdapter
        recyclerViewState?.let {
            courseVideoRecycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }
}