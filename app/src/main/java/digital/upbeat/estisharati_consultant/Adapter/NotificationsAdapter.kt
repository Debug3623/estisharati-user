package digital.upbeat.estisharati_consultant.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_consultant.DataClassHelper.Notification.Data
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.UI.NotificationDetails
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
        if (notificationsArrayList.get(position).details != null) {
            notificationsArrayList.get(position).details?.let {
                if (it.get(0).type.equals("course")) {
                    holder.notificationImg.setImageResource(R.drawable.ic_courses_round)
                } else if (it.get(0).type.equals("consultant")) {
                    holder.notificationImg.setImageResource(R.drawable.ic_consultant_round)
                } else {
                    holder.notificationImg.setImageResource(R.mipmap.ic_launcher)
                }
            }
        } else {
            holder.notificationImg.setImageResource(R.mipmap.ic_launcher)
        }
        if (notificationsArrayList.get(position).details!=null&&notificationsArrayList.get(position).details!!.size > 0) {
            holder.rightArrow.visibility = View.VISIBLE
        } else {
            holder.rightArrow.visibility = View.GONE
        }

        holder.notification_parant.setOnClickListener {
            if (notificationsArrayList.get(position).details!=null&&notificationsArrayList.get(position).details!!.size > 0) {
                val intent = Intent(context, NotificationDetails::class.java)
                intent.putExtra("position", position)
                context.startActivity(intent)
            }
        }
    }
}
