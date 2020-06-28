package digital.upbeat.estisharati_user.Adapter

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
import digital.upbeat.estisharati_user.DataClassHelper.DataUserMessageFireStore
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import digital.upbeat.estisharati_user.ViewHolder.*

class RecentChatAdapter(val context: Context, val chatHome: ChatHome, val dataUserMessageFireStoreArrayList: ArrayList<DataUserMessageFireStore>) : RecyclerView.Adapter<RecentChatViewHolder>() {
    val helperMethods: HelperMethods
    val almarai_light: Typeface
    val almarai_regular: Typeface

    init {
        helperMethods = HelperMethods(context)
        almarai_light = ResourcesCompat.getFont(context, R.font.almarai_light)!!
        almarai_regular = ResourcesCompat.getFont(context, R.font.almarai_regular)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.recent_chat_item, parent, false)
        return RecentChatViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return dataUserMessageFireStoreArrayList.size
    }

    override fun onBindViewHolder(holder: RecentChatViewHolder, position: Int) {
        val dataUserMessageFireStore = dataUserMessageFireStoreArrayList.get(position)
        var count = 0
        for (dataMsg in dataUserMessageFireStore.messagesArrayList) {
            if (dataMsg.receiver_id.equals(chatHome.dataUser.id) && (dataMsg.message_status.equals("send") || dataMsg.message_status.equals("delivered"))) {
                count++
            }
        }
        Glide.with(context).load(dataUserMessageFireStore.dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.profile_picture)
        if (0 < count) {
            holder.unread_message_count.text = count.toString()
            holder.unread_message_count.visibility = View.VISIBLE
            holder.profile_picture.borderWidth = 5
        } else {
            holder.unread_message_count.visibility = View.GONE
            holder.profile_picture.borderWidth = 0
        }

        if (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).receiver_id.equals(chatHome.dataUser.id) && (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_status.equals("send") || dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_status.equals("delivered"))) {
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
        holder.user_name.text = "${dataUserMessageFireStore.dataUserFireStore.fname} ${dataUserMessageFireStore.dataUserFireStore.lname}"
        holder.last_message_time.text = helperMethods.getFormattedDate(dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).send_time)


        if (dataUserMessageFireStore.dataUserFireStore.online_status) holder.online_status.visibility = View.VISIBLE else holder.online_status.visibility = View.GONE
        holder.parent_layout.setOnClickListener {
            val intent = Intent(context, ChatPage::class.java)
            intent.putExtra("user_id", dataUserMessageFireStore.dataUserFireStore.user_id)
            context.startActivity(intent)
        }
    }
}