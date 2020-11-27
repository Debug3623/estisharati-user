package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.CourseDetails.Lesson
import digital.upbeat.estisharati_user.Fragment.CourseContent
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.ViewHolder.*

class CourseContentSubAdapter(val context: Context, val courseContent: CourseContent, val lessons: ArrayList<Lesson>) : RecyclerView.Adapter<CourseContentSubViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseContentSubViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.course_content_sub_item, parent, false)
        return CourseContentSubViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: CourseContentSubViewHolder, position: Int) {
        holder.lessonTitle.text = lessons.get(position).title
    }
}
