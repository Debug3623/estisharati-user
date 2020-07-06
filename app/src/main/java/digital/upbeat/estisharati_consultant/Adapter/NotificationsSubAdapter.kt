package digital.upbeat.estisharati_consultant.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.UI.NotificationDetails
import digital.upbeat.estisharati_consultant.ViewHolder.NotificationsReplyViewHolder

class NotificationsSubAdapter(val context: Context,val notificationsDetails: NotificationDetails?, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<NotificationsReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsReplyViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.notification_sub_item, parent, false)
        return NotificationsReplyViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: NotificationsReplyViewHolder, position: Int) {

    }
}
