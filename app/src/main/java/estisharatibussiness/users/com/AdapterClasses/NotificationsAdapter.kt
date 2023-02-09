package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.DataClassHelperMehtods.Notification.Data
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityConsultantDetails
import estisharatibussiness.users.com.UserInterfaces.ActivityCourseDetails
import estisharatibussiness.users.com.UserInterfaces.Notifications
import estisharatibussiness.users.com.PublicViewHolder.NotificationsViewHolder

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

        holder.notification_parant.setOnClickListener {
            notificationsArrayList.get(position).details?.let {
                if (it.get(0).type.equals("course")) {
                    val intent = Intent(context, ActivityCourseDetails::class.java)
                    intent.putExtra("courseId", it.get(0).course_id)
                    context.startActivity(intent)
                } else if (it.get(0).type.equals("consultant")) {

                    Log.d("consultants move","consultants notification press")
//                    val intent = Intent(context, ActivityConsultantDetails::class.java)
//                    intent.putExtra("consultant_id", it[0].consultant_id)
//                    intent.putExtra("category_id", "")
//                    //test data
//                    intent.putExtra("appointment_date", "")
//                    intent.putExtra("appointment_time", "")
//                    intent.putExtra("consultant_id", "")
//                    intent.putExtra("category_id", "")
//                    intent.putExtra("condition", "0")
//                    intent.putExtra("chat", "")
//                    intent.putExtra("audio", "")
//                    intent.putExtra("video", "")
//                    intent.putExtra("transaction_amount", "")
//
//                    context.startActivity(intent)
                } else {
                }
            }
        }
    }
}
