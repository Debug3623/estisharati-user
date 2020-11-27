package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.online_courses_item.view.*

class OnlineCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
val  parentLayout=itemView.parentLayout
val  courseImage=itemView.courseImage
val  courseName=itemView.courseName
val  coursePrice=itemView.coursePrice
val  courseRating=itemView.courseRating
val  coursePeriod=itemView.coursePeriod
}