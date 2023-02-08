package estisharati.bussiness.eshtisharati_consultants.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharati.bussiness.eshtisharati_consultants.DataClassHelper.Notification.Reply
import estisharati.bussiness.eshtisharati_consultants.R
import estisharati.bussiness.eshtisharati_consultants.UI.NotificationDetails
import estisharati.bussiness.eshtisharati_consultants.ViewHolder.NotificationsReplyViewHolder
import kotlinx.android.synthetic.main.activity_notification_details.*

class NotificationsSubAdapter(val context: Context, val notificationsDetails: NotificationDetails, val replies: ArrayList<Reply>) : RecyclerView.Adapter<NotificationsReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsReplyViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.notification_sub_item, parent, false)
        return NotificationsReplyViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return replies.size
    }

    override fun onBindViewHolder(holder: NotificationsReplyViewHolder, position: Int) {
        holder.replyUserName.text = replies.get(position).user.name
        holder.replyTime.text = replies.get(position).created_at
        holder.reply.text = replies.get(position).comment
        holder.reply.setOnClickListener { notificationsDetails.helperMethods.AlertPopup(replies.get(position).user.name, replies.get(position).comment) }
        Glide.with(context).load(replies.get(position).user.image_path).apply(notificationsDetails.helperMethods.requestOption).into(holder.replyImage)
    }
}
