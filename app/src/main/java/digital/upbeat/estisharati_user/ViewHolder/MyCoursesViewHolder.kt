package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.my_courses_item.view.*

class MyCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
val courses_name=itemView.courses_name
val complete=itemView.complete
val courseImage=itemView.courseImage
val reviewCourse=itemView.reviewCourse
val start_course=itemView.start_course
val add_review_layout=itemView.add_review_layout
val myCourseLayout=itemView.myCourseLayout

}