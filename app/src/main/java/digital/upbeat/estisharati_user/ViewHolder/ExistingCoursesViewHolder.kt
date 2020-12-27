package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.existing_courses_item.view.*

class ExistingCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
val courseImage=itemView.courseImage
val courseName=itemView.courseName
val courseRating=itemView.courseRating
val coursePrice=itemView.coursePrice
val courseLayout=itemView.courseLayout
}