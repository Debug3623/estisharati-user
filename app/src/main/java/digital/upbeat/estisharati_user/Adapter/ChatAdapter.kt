package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.DataMessageFireStore
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ChatPage
import digital.upbeat.estisharati_user.UI.PhotoViewer
import digital.upbeat.estisharati_user.ViewHolder.ChatViewHolder
import kotlinx.android.synthetic.main.activity_chat_page.*

class ChatAdapter(val context: Context, val chatPage: ChatPage, val messagesArrayList: ArrayList<DataMessageFireStore>) : RecyclerView.Adapter<ChatViewHolder>() {
    val helperMethods: HelperMethods
    var replyPositionClicked = ""

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
                holder.other_text_layout.visibility = View.GONE
                holder.me_image_hole_layout.visibility = View.GONE
                holder.other_image_hole_layout.visibility = View.GONE

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
                if (dataMessageFireStore.message_other_type.equals("forwarded")) {
                    holder.me_text_is_forward.visibility = View.VISIBLE
                } else {
                    holder.me_text_is_forward.visibility = View.GONE
                }
                //*********reply message*****
                setReplyMessage(dataMessageFireStore, holder.me_text_reply_layout, holder.me_text_reply_from, holder.me_text_reply_text, holder.me_text_reply_image_layout, holder.me_text_reply_image, holder.me_text_reply_view_line)
            } else {
                holder.other_text_layout.visibility = View.VISIBLE
                holder.me_text_layout.visibility = View.GONE
                holder.me_image_hole_layout.visibility = View.GONE
                holder.other_image_hole_layout.visibility = View.GONE

                Glide.with(context).load(chatPage.dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.other_text_profile)
                holder.other_text.text = dataMessageFireStore.message_content
                holder.other_text_time.text = helperMethods.getFormattedDate(dataMessageFireStore.send_time)
                if (dataMessageFireStore.message_other_type.equals("forwarded")) {
                    holder.other_text_is_forward.visibility = View.VISIBLE
                } else {
                    holder.other_text_is_forward.visibility = View.GONE
                }
                setReplyMessage(dataMessageFireStore, holder.other_text_reply_layout, holder.other_text_reply_from, holder.other_text_reply_text, holder.other_text_reply_image_layout, holder.other_text_reply_image, holder.other_text_reply_view_line)
            }
        } else if (dataMessageFireStore.message_type.equals("image")) {
            if (dataMessageFireStore.sender_id.equals(chatPage.dataUser.id)) {
                holder.me_image_hole_layout.visibility = View.VISIBLE
                holder.other_image_hole_layout.visibility = View.GONE
                holder.other_text_layout.visibility = View.GONE
                holder.me_text_layout.visibility = View.GONE
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
                if (dataMessageFireStore.message_other_type.equals("forwarded")) {
                    holder.me_image_is_forward.visibility = View.VISIBLE
                } else {
                    holder.me_image_is_forward.visibility = View.GONE
                }
                setReplyMessage(dataMessageFireStore, holder.me_image_reply_layout, holder.me_image_reply_from, holder.me_image_reply_text, holder.me_image_reply_image_layout, holder.me_image_reply_image, holder.me_text_reply_view_line)
            } else {
                holder.other_image_hole_layout.visibility = View.VISIBLE
                holder.other_text_layout.visibility = View.GONE
                holder.me_text_layout.visibility = View.GONE
                holder.me_image_hole_layout.visibility = View.GONE
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
                if (dataMessageFireStore.message_other_type.equals("forwarded")) {
                    holder.other_image_is_forward.visibility = View.VISIBLE
                } else {
                    holder.other_image_is_forward.visibility = View.GONE
                }
                setReplyMessage(dataMessageFireStore, holder.other_image_reply_layout, holder.other_image_reply_from, holder.other_image_reply_text, holder.other_image_reply_image_layout, holder.other_image_reply_image, holder.other_text_reply_view_line)
            }
        }
        if (!replyPositionClicked.equals("") && replyPositionClicked.toInt() == position) {
       changeBackgroundColor(holder)
        }
        holder.other_forward_text.visibility = View.GONE
        holder.other_forward_image.visibility = View.GONE
        holder.me_forward_text.visibility = View.GONE
        holder.me_forward_image.visibility = View.GONE

        holder.other_text_layout.setOnLongClickListener {
            holder.other_forward_text.visibility = View.VISIBLE
            false
        }
        holder.other_image_hole_layout.setOnLongClickListener {
            holder.other_forward_image.visibility = View.VISIBLE
            false
        }
        holder.other_image_layout.setOnLongClickListener {
            holder.other_forward_image.visibility = View.VISIBLE
            false
        }
        holder.me_text_layout.setOnLongClickListener {
            holder.me_forward_text.visibility = View.VISIBLE
            false
        }
        holder.me_image_layout.setOnLongClickListener {
            holder.me_forward_image.visibility = View.VISIBLE
            false
        }
        holder.me_image_hole_layout.setOnLongClickListener {
            holder.me_forward_image.visibility = View.VISIBLE
            false
        }

        holder.other_forward_text.setOnClickListener {
            sendForwardMessage(dataMessageFireStore)
        }
        holder.other_forward_image.setOnClickListener {
            sendForwardMessage(dataMessageFireStore)
        }
        holder.me_forward_text.setOnClickListener {
            sendForwardMessage(dataMessageFireStore)
        }
        holder.me_forward_image.setOnClickListener {
            sendForwardMessage(dataMessageFireStore)
        }
    }

    fun sendForwardMessage(dataMessageFireStore: DataMessageFireStore) {
        GlobalData.forwardType = dataMessageFireStore.message_type
        GlobalData.forwardContent = dataMessageFireStore.message_content
        chatPage.finish()
        helperMethods.showToastMessage("Now you can forward the message!")
    }

    fun changeBackgroundColor(holder: ChatViewHolder) {
        holder.parent_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.pink))
        object : CountDownTimer(1000, 1000) {
            override fun onFinish() {
                replyPositionClicked = ""
                holder.parent_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            }

            override fun onTick(millisUntilFinished: Long) {
            }
        }.start()
    }

    fun setReplyMessage(dataMessageFireStore: DataMessageFireStore, reply_layout: LinearLayout, reply_from: TextView, reply_text: TextView, reply_image_layout: LinearLayout, reply_image: ImageView, view_line: View) {
        //*********reply message*****
        if (dataMessageFireStore.inside_reply.containsKey("message_id") && !dataMessageFireStore.inside_reply.getValue("message_id").isEmpty()) {
            reply_layout.visibility = View.VISIBLE
            view_line.visibility = View.VISIBLE
            if (dataMessageFireStore.inside_reply.getValue("sender_id").equals(chatPage.dataUser.id)) {
                reply_from.text = "You"
                reply_from.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else {
                reply_from.text = chatPage.dataUserFireStore.fname + " " + chatPage.dataUserFireStore.lname
                reply_from.setTextColor(ContextCompat.getColor(context, R.color.orange))
            }
            if (dataMessageFireStore.inside_reply.getValue("message_type").equals("text")) {
                reply_text.text = dataMessageFireStore.inside_reply.getValue("message_content")
                reply_text.visibility = View.VISIBLE
                reply_image_layout.visibility = View.GONE
            } else if (dataMessageFireStore.inside_reply.getValue("message_type").equals("image")) {
                Glide.with(context).load(dataMessageFireStore.inside_reply.getValue("message_content")).apply(helperMethods.requestOption).into(reply_image)
                reply_image_layout.visibility = View.VISIBLE
                reply_text.visibility = View.GONE
            }
            reply_layout.setOnClickListener {
                val position = dataMessageFireStore.inside_reply.getValue("position")
                (chatPage.chat_recycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position.toInt(), 600)
                chatPage.showChatScrollToBottom(position.toInt())
               val viewHolder :ViewHolder? =chatPage.chat_recycler.findViewHolderForAdapterPosition(position.toInt())
               if(viewHolder!=null){
                   changeBackgroundColor(viewHolder as ChatViewHolder)
                   Log.d("viewHoleder","not null")
               }else{
                   replyPositionClicked = position
                   Log.d("viewHoleder","is null")
               }
                viewHolder?.let {
                }
            }
        } else {
            reply_layout.visibility = View.GONE
            view_line.visibility = View.GONE
        }
    }
}
