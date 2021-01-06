package digital.upbeat.estisharati_consultant.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_consultant.DataClassHelper.Notification.Data
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.UI.Notifications
import digital.upbeat.estisharati_consultant.ViewHolder.NotificationsViewHolder

class NotificationsAdapter(val context: Context, val notifications: Notifications, val notificationsArrayList: ArrayList<Data>) : RecyclerView.Adapter<NotificationsViewHolder>() {
    val helperMethods: HelperMethods

    init {
        helperMethods = HelperMethods(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false)
        return NotificationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return notificationsArrayList.size
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.title.text = notificationsArrayList.get(position).title
        holder.message.text = notifications.helperMethods.getHtmlText(notificationsArrayList.get(position).body)
        holder.date.text = notificationsArrayList.get(position).created_at
        holder.notificationImg.setImageResource(R.mipmap.ic_launcher)


        holder.notification_parant.setOnClickListener {}
    }
}
