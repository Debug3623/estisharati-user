package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.course_lesson_item.view.*

class CourseLessonVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val lessonsTitle = itemView.lessonsTitle
    val lessonsPlaying = itemView.lessonsPlaying
    val lessonsLayout = itemView.lessonsLayout
    val lessonsDownload = itemView.lessonsDownload
}