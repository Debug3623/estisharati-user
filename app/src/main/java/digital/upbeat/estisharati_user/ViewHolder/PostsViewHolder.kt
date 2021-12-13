package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.testimonials_item.view.*

class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val holeLayout = itemView.holeLayout
    val profilePicture = itemView.profilePicture
    val userName = itemView.userName
    val commentsCount = itemView.commentsCount
    val testimonialType = itemView.testimonialType
    val testimonialsContent = itemView.testimonialsContent
}