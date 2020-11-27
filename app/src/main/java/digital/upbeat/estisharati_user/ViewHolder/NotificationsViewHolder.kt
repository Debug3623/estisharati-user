package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_item.view.*
import kotlinx.android.synthetic.main.online_user_item.view.*

class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val message = itemView.message
    val notification_parant = itemView.notification_parant
}