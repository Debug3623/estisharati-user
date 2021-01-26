package digital.upbeat.estisharati_consultant.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_consultant.DataClassHelper.RecentChat.DataUserMessageFireStore
import digital.upbeat.estisharati_consultant.Fragment.Subscribers
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.UI.ChatPage
import digital.upbeat.estisharati_consultant.ViewHolder.SubscribersViewHolder

class SubscribersAdapter(val context: Context, val subscribers: Subscribers, val dataUserMessageFireStoreArrayList: ArrayList<DataUserMessageFireStore>) : RecyclerView.Adapter<SubscribersViewHolder>() {
    val helperMethods: HelperMethods
    val almarai_light: Typeface
    val almarai_regular: Typeface

    init {
        helperMethods = HelperMethods(context)
        almarai_light = ResourcesCompat.getFont(context, R.font.almarai_light)!!
        almarai_regular = ResourcesCompat.getFont(context, R.font.almarai_regular)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribersViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.subscribers_recent_chat_item, parent, false)
        return SubscribersViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return dataUserMessageFireStoreArrayList.size
    }

    override fun onBindViewHolder(holder: SubscribersViewHolder, position: Int) {
        val dataUserMessageFireStore = dataUserMessageFireStoreArrayList.get(position)
        var count = 0
        for (dataMsg in dataUserMessageFireStore.messagesArrayList) {
            if (dataMsg.receiver_id.equals(subscribers.dataUser.id) && (dataMsg.message_status.equals("send") || dataMsg.message_status.equals("delivered"))) {
                count++
            }
        }
        holder.user_name.text = "${dataUserMessageFireStore.dataUserFireStore.fname} ${dataUserMessageFireStore.dataUserFireStore.lname}"
        Glide.with(context).load(dataUserMessageFireStore.dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.profile_picture)
        if (0 < count) {
            holder.unread_message_count.text = if (count > 50) "50+" else count.toString()
            holder.unread_message_count.visibility = View.VISIBLE
            holder.profile_picture.borderWidth = 8
        } else {
            holder.unread_message_count.visibility = View.GONE
            holder.profile_picture.borderWidth = 0
        }
        if (dataUserMessageFireStore.dataUserFireStore.user_type.equals("user")) {
            holder.nectie.visibility = View.GONE
        } else if (dataUserMessageFireStore.dataUserFireStore.user_type.equals("consultant")) {
            holder.nectie.visibility = View.VISIBLE
        }



        if (dataUserMessageFireStore.messagesArrayList.size > 0) {
            if (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).receiver_id.equals(subscribers.dataUser.id) && (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_status.equals("send") || dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_status.equals("delivered"))) {
                if (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_type.equals("text")) {
                    holder.last_message.visibility = View.VISIBLE
                    holder.last_image_layout.visibility = View.GONE
                    holder.last_message.typeface = almarai_regular
                    holder.last_message.setTextColor(ContextCompat.getColor(context, R.color.black))
                } else if (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_type.equals("image")) {
                    holder.last_image_layout.visibility = View.VISIBLE
                    holder.last_message.visibility = View.GONE
                    holder.last_image.setImageResource(R.drawable.ic_photo_black)
                    holder.last_image_name.typeface = almarai_regular
                    holder.last_image_name.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            } else {
                if (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_type.equals("text")) {
                    holder.last_message.visibility = View.VISIBLE
                    holder.last_image_layout.visibility = View.GONE
                    holder.last_message.typeface = almarai_light
                    holder.last_message.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
                } else if (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_type.equals("image")) {
                    holder.last_image_layout.visibility = View.VISIBLE
                    holder.last_message.visibility = View.GONE
                    holder.last_image.setImageResource(R.drawable.ic_photo_darkgray)
                    holder.last_image_name.typeface = almarai_light
                    holder.last_image_name.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
                }
            }

            holder.last_message.text = dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_content
            holder.last_message_time.text = helperMethods.getFormattedDateShort(dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).send_time)
            if (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).sender_id.equals(subscribers.dataUser.id)) {
                holder.image_message_status.visibility = View.VISIBLE
                when (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_status) {
                    "send" -> {
                        holder.image_message_status.setImageResource(R.drawable.ic_sended)
                    }
                    "delivered" -> {
                        holder.image_message_status.setImageResource(R.drawable.ic_delivered)
                    }
                    "seened" -> {
                        holder.image_message_status.setImageResource(R.drawable.ic_seened)
                    }
                    else -> {
                    }
                }
            } else {
                holder.image_message_status.visibility = View.GONE
            }
        } else {
            holder.image_message_status.visibility = View.GONE
            holder.last_image_layout.visibility = View.GONE
            holder.last_message.visibility = View.GONE
        }

        if (dataUserMessageFireStore.dataUserFireStore.online_status) holder.online_status.visibility = View.VISIBLE else holder.online_status.visibility = View.GONE
        holder.parent_layout.setOnClickListener {
            val intent = Intent(context, ChatPage::class.java)
            intent.putExtra("user_id", dataUserMessageFireStore.dataUserFireStore.user_id)
            intent.putExtra("forward_type", GlobalData.forwardType)
            intent.putExtra("forward_content", GlobalData.forwardContent)
            context.startActivity(intent)
            GlobalData.forwardType = ""
            GlobalData.forwardContent = ""
        }
    }
}
