package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.comments_reply_sub_item.view.*

class ConsultantCommentsSubReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val subReplyUserImage=itemView.subReplyUserImage
    val subReplyUserName=itemView.subReplyUserName
    val subReplyDateTime=itemView.subReplyDateTime
    val subReplyMessage=itemView.subReplyMessage
    val nectie=itemView.nectie
}