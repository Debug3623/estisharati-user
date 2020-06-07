package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.CourseContent
import digital.upbeat.estisharati_user.Fragment.PreviewCourseContent
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.MyConsultations
import digital.upbeat.estisharati_user.UI.MyCourses
import digital.upbeat.estisharati_user.UI.OnlineCourses
import digital.upbeat.estisharati_user.ViewHolder.*
import kotlinx.android.synthetic.main.fragment_comments.*

class PreviewCourseContentAdapter(val context: Context, val previewCourseContent: PreviewCourseContent, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<PreviewCourseContentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewCourseContentViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.preview_course_content_item, parent, false)
        return PreviewCourseContentViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: PreviewCourseContentViewHolder, position: Int) {


        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")


        holder.course_content_sub_recycler.setHasFixedSize(true)
        holder.course_content_sub_recycler.removeAllViews()
        holder.course_content_sub_recycler.layoutManager = LinearLayoutManager(context)
        holder.course_content_sub_recycler.adapter = PreviewCourseContentSubAdapter(context, previewCourseContent, arrayList)

    }
}
