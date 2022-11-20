package estisharatibussiness.users.com.AdapterClasses

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
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataUserMessageFireStore
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.*
import estisharatibussiness.users.com.PublicViewHolder.*

class RecentChatAdapter(val context: Context, val chatHomeActivity: ChatHomeActivity, val dataUserMessageFireStoreArrayList: ArrayList<DataUserMessageFireStore>) : RecyclerView.Adapter<RecentChatViewHolder>() {
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
            if (dataMsg.receiver_id.equals(chatHomeActivity.dataUser.id) && (dataMsg.message_status.equals("send") || dataMsg.message_status.equals("delivered"))) {
                count++
            }
        }
        if (0 < count) {
            holder.unread_message_count.text = if (count > 50) "50+" else count.toString()

            holder.unread_message_count.visibility = View.VISIBLE
            holder.profile_picture.borderWidth = 8
        } else {
            holder.unread_message_count.visibility = View.GONE
            holder.profile_picture.borderWidth = 0
        }
        Glide.with(context).load(dataUserMessageFireStore.dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.profile_picture)

        if (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).receiver_id.equals(chatHomeActivity.dataUser.id) && (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_status.equals("send") || dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_status.equals("delivered"))) {
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

        if (dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).sender_id.equals(chatHomeActivity.dataUser.id)) {
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


        holder.last_message.text = dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).message_content
        holder.user_name.text = "${dataUserMessageFireStore.dataUserFireStore.fname} ${dataUserMessageFireStore.dataUserFireStore.lname}"
        holder.last_message_time.text = helperMethods.getFormattedDateShort(dataUserMessageFireStore.messagesArrayList.get(dataUserMessageFireStore.messagesArrayList.lastIndex).send_time)

        if (dataUserMessageFireStore.dataUserFireStore.user_type.equals("user")) {
            holder.nectie.visibility = View.GONE
        } else if (dataUserMessageFireStore.dataUserFireStore.user_type.equals("consultant")) {
            holder.nectie.visibility = View.VISIBLE
        }
        if (dataUserMessageFireStore.dataUserFireStore.online_status) holder.online_status.visibility = View.VISIBLE else holder.online_status.visibility = View.GONE
        holder.parent_layout.setOnClickListener {
            val intent = Intent(context, ChatPageActivity::class.java)
            intent.putExtra("user_id", dataUserMessageFireStore.dataUserFireStore.user_id)
            intent.putExtra("forward_type", GlobalData.forwardType)
            intent.putExtra("forward_content", GlobalData.forwardContent)

            context.startActivity(intent)
            GlobalData.forwardType = ""
            GlobalData.forwardContent = ""
        }
    }
}
