package estisharatibussiness.users.com.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.course_content_item.view.*

class CourseContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val child_layout=itemView.child_layout
    val expand_icon=itemView.expand_icon
    val chapter_title=itemView.chapter_title
    val course_content_sub_recycler=itemView.courseLessonRecycler
}