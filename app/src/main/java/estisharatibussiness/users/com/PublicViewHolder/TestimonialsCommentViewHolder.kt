package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.testimonials_comment_item.view.*

class TestimonialsCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
val profilePicture=itemView.profilePicture
val userName=itemView.userName
val createdAt=itemView.createdAt
val comment=itemView.comment
}