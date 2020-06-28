package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.DataMessageFireStore
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.CourseContent
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import digital.upbeat.estisharati_user.ViewHolder.*

class ChatAdapter(val context: Context, val chatPage: ChatPage, val messagesArrayList: ArrayList<DataMessageFireStore>) : RecyclerView.Adapter<ChatViewHolder>() {
    val helperMethods: HelperMethods

    init {
        helperMethods = HelperMethods(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return messagesArrayList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val dataMessageFireStore = messagesArrayList.get(position)

        if (dataMessageFireStore.message_type.equals("text")) {
            if (dataMessageFireStore.sender_id.equals(chatPage.dataUser.id)) {
                holder.me_text_layout.visibility = View.VISIBLE
                Glide.with(context).load(chatPage.dataUser.image).apply(helperMethods.profileRequestOption).into(holder.me_text_profile)
                holder.me_text.text = dataMessageFireStore.message_content
                holder.me_text_time.text = helperMethods.getFormattedDate(dataMessageFireStore.send_time)
                when (dataMessageFireStore.message_status) {
                    "send" -> {
                        holder.text_message_status.setImageResource(R.drawable.ic_sended)
                    }
                    "delivered" -> {
                        holder.text_message_status.setImageResource(R.drawable.ic_delivered)
                    }
                    "seened" -> {
                        holder.text_message_status.setImageResource(R.drawable.ic_seened)
                    }
                    else -> {
                    }
                }
            } else {
                holder.other_text_layout.visibility = View.VISIBLE
                Glide.with(context).load(chatPage.dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.other_text_profile)
                holder.other_text.text = dataMessageFireStore.message_content
                holder.other_text_time.text = helperMethods.getFormattedDate(dataMessageFireStore.send_time)
            }
        } else if (dataMessageFireStore.message_type.equals("image")) {
            if (dataMessageFireStore.sender_id.equals(chatPage.dataUser.id)) {
                holder.me_image_layout_hole.visibility = View.VISIBLE
                Glide.with(context).load(chatPage.dataUser.image).apply(helperMethods.profileRequestOption).into(holder.me_image_profile)
                Glide.with(context).load(dataMessageFireStore.message_content).apply(helperMethods.requestOption).into(holder.me_image)
                holder.me_image_time.text = helperMethods.getFormattedDate(dataMessageFireStore.send_time)
                holder.me_image_layout.setOnClickListener {
                    val intent = Intent(context, PhotoViewer::class.java)
                    intent.putExtra("name", "${chatPage.dataUser.fname} ${chatPage.dataUser.lname}")
                    intent.putExtra("image_url", dataMessageFireStore.message_content)
                    intent.putExtra("send_time", helperMethods.getFormattedDate(dataMessageFireStore.send_time))
                    context.startActivity(intent)
                }
                when (dataMessageFireStore.message_status) {
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
                holder.other_image_hole_layout.visibility = View.VISIBLE
                Glide.with(context).load(chatPage.dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.other_image_profile)
                Glide.with(context).load(dataMessageFireStore.message_content).apply(helperMethods.requestOption).into(holder.other_image)
                holder.other_image_time.text = helperMethods.getFormattedDate(dataMessageFireStore.send_time)
                holder.other_image_layout.setOnClickListener {
                    val intent = Intent(context, PhotoViewer::class.java)
                    intent.putExtra("name", "${chatPage.dataUserFireStore.fname} ${chatPage.dataUserFireStore.lname}")
                    intent.putExtra("image_url", dataMessageFireStore.message_content)
                    intent.putExtra("send_time", helperMethods.getFormattedDate(dataMessageFireStore.send_time))
                    context.startActivity(intent)
                }
            }
        }
    }
}
