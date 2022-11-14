package estisharatibussiness.users.com.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.favorite_courses_item.view.*

class FavoriteCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val courseName=itemView.courseName
    val courseImage=itemView.courseImage
    val coursePrice=itemView.coursePrice
    val courseRating=itemView.courseRating
    val coursePeriod=itemView.coursePeriod
    val parentLayout=itemView.parentLayout
}