package digital.upbeat.estisharati_consultant.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_item.view.*

class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val message = itemView.notification_message
    val date = itemView.notification_date
    val title = itemView.notification_title
    val rightArrow = itemView.rightArrow
    val notificationImg = itemView.notificationImg
    val notification_parant = itemView.notification_parant
}