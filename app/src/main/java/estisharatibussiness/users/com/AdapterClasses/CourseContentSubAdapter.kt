package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.DataClassHelperMehtods.CourseDetails.Lesson
import estisharatibussiness.users.com.FragmentClasses.CourseContent
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.PublicViewHolder.*

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
