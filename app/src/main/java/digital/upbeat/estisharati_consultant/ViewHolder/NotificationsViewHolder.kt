package digital.upbeat.estisharati_consultant.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_item.view.*

class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
val message=itemView.message
val notifications_parent=itemView.notifications_parent
}