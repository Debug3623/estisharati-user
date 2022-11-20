package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.comment_reply_item.view.*

class ConsultantCommentsReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val consultant_comments_reply_sub_recycler = itemView.consultant_comments_reply_sub_recycler
    val sub_item_reply_layout = itemView.sub_item_reply_layout
    val main_item = itemView.main_item
    val cmdUserImage = itemView.cmdUserImage
    val cmdUserName = itemView.cmdUserName
    val cmdUserRating = itemView.cmdUserRating
    val cmdReplyCount = itemView.cmdReplyCount
    val cmdDateTime = itemView.cmdDateTime
    val cmdMessage = itemView.cmdMessage
    val userImage = itemView.userImage
    val commentsReply = itemView.commentsReply
    val submitReplay = itemView.submitReplay
}