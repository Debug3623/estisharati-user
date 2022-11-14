package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.DataClassHelper.CourseDetails.Lesson
import estisharatibussiness.users.com.Fragment.CourseContent
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.ViewHolder.*

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
