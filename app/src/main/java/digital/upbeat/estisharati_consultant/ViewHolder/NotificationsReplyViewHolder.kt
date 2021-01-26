package digital.upbeat.estisharati_consultant.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_sub_item.view.*

class NotificationsReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val replyImage = itemView.replyImage
    val replyUserName = itemView.replyUserName
    val replyTime = itemView.replyTime
    val reply = itemView.reply
}