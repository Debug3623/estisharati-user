package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.exp_courses_item.view.*

class ExpCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val parentLayout=itemView.parentLayout
    val courseName=itemView.courseName
    val courseImage=itemView.courseImage
    val coursePrice=itemView.coursePrice
}