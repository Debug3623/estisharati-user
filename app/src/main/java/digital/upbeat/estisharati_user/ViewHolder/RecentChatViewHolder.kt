package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recent_chat_item.view.*

class RecentChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
val parent_layout=itemView.parentLayout
val profile_picture=itemView.profile_picture
val unread_message_count=itemView.unread_message_count
val user_name=itemView.user_name
val last_message=itemView.last_message
val last_message_time=itemView.last_seen_time
val online_status=itemView.online_status
val last_image_layout=itemView.last_image_layout
val last_image=itemView.last_image
val last_image_name=itemView.last_image_name
val image_message_status=itemView.image_message_status
val nectie=itemView.nectie

}