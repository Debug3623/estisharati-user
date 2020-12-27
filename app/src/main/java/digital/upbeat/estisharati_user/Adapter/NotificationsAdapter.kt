package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Notification.Data
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.UI.CourseDetails
import digital.upbeat.estisharati_user.UI.Notifications
import digital.upbeat.estisharati_user.ViewHolder.NotificationsViewHolder

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
        holder.message.text = notificationsArrayList.get(position).body
        holder.date.text = notificationsArrayList.get(position).created_at

        if (notificationsArrayList.get(position).details.type.equals("course")) {
            holder.notificationImg.setImageResource(R.drawable.ic_courses_round)
        } else if (notificationsArrayList.get(position).details.type.equals("consultant")) {
            holder.notificationImg.setImageResource(R.drawable.ic_consultant_round)
        } else {
            holder.notificationImg.setImageResource(R.mipmap.ic_launcher)
        }

        holder.notification_parant.setOnClickListener {
            if (notificationsArrayList.get(position).details.type.equals("course")) {
                val intent = Intent(context, CourseDetails::class.java)
                intent.putExtra("courseId", notificationsArrayList.get(position).details.course_id)
                context.startActivity(intent)
            } else if (notificationsArrayList.get(position).details.type.equals("consultant")) {
                val intent = Intent(context, ConsultantDetails::class.java)
                intent.putExtra("consultant_id", notificationsArrayList.get(position).details.consultant_id)
                context.startActivity(intent)
            } else {

            }
        }
    }
}
