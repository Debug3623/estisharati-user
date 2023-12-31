package estisharatibussiness.users.com.AdapterClasses

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
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.ChatGroupMessage.GroupMessageFireStore
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.*
import estisharatibussiness.users.com.PublicViewHolder.*
import kotlinx.android.synthetic.main.activity_group_chat_page.*

class GroupChatAdapter(val context: Context, val activityGroupChatPage: ActivityGroupChatPage, val messagesArrayList: ArrayList<GroupMessageFireStore>) : RecyclerView.Adapter<GroupChatViewHolder>() {
    val helperMethods: HelperMethods
    var replyPositionClicked = ""

    init {
        helperMethods = HelperMethods(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.group_chat_item, parent, false)
        return GroupChatViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return messagesArrayList.size
    }

    override fun onBindViewHolder(holder: GroupChatViewHolder, position: Int) {
        val groupMessageFireStore = messagesArrayList.get(position)

        if (groupMessageFireStore.message_type.equals("text")) {
            if (groupMessageFireStore.sender_id.equals(activityGroupChatPage.dataUser.id)) {
                holder.me_text_layout.visibility = View.VISIBLE
                holder.other_text_layout.visibility = View.GONE
                holder.me_image_hole_layout.visibility = View.GONE
                holder.other_image_hole_layout.visibility = View.GONE

                Glide.with(context).load(activityGroupChatPage.dataUser.image).apply(helperMethods.profileRequestOption).into(holder.me_text_profile)
                holder.me_text_sender_name.text=activityGroupChatPage.dataUser.fname+" "+activityGroupChatPage.dataUser.lname
                if (activityGroupChatPage.groupItemData.creater_id.equals(groupMessageFireStore.sender_id)) {
                    holder.me_text_nectie.visibility = View.VISIBLE
                } else {
                    holder.me_text_nectie.visibility = View.GONE
                }

                holder.me_text.text = groupMessageFireStore.message_content
                holder.me_text_time.text = helperMethods.getFormattedDate(groupMessageFireStore.send_time)

                if (groupMessageFireStore.message_other_type.equals("forwarded")) {
                    holder.me_text_is_forward.visibility = View.VISIBLE
                } else {
                    holder.me_text_is_forward.visibility = View.GONE
                }
                //*********reply message*****
                setReplyMessage(groupMessageFireStore, holder.me_text_reply_layout, holder.me_text_reply_from, holder.me_text_reply_text, holder.me_text_reply_image_layout, holder.me_text_reply_image, holder.me_text_reply_view_line)
            } else {
                holder.other_text_layout.visibility = View.VISIBLE
                holder.me_text_layout.visibility = View.GONE
                holder.me_image_hole_layout.visibility = View.GONE
                holder.other_image_hole_layout.visibility = View.GONE

                Glide.with(context).load(activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.sender_id).image).apply(helperMethods.profileRequestOption).into(holder.other_text_profile)
                holder.other_text_sender_name.text=activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.sender_id).fname+" "+activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.sender_id).lname

                if (activityGroupChatPage.groupItemData.creater_id.equals(groupMessageFireStore.sender_id)) {
                    holder.other_text_nectie.visibility = View.VISIBLE
                } else {
                    holder.other_text_nectie.visibility = View.GONE
                }

                holder.other_text.text = groupMessageFireStore.message_content
                holder.other_text_time.text = helperMethods.getFormattedDate(groupMessageFireStore.send_time)
                if (groupMessageFireStore.message_other_type.equals("forwarded")) {
                    holder.other_text_is_forward.visibility = View.VISIBLE
                } else {
                    holder.other_text_is_forward.visibility = View.GONE
                }
                setReplyMessage(groupMessageFireStore, holder.other_text_reply_layout, holder.other_text_reply_from, holder.other_text_reply_text, holder.other_text_reply_image_layout, holder.other_text_reply_image, holder.other_text_reply_view_line)
            }
        } else if (groupMessageFireStore.message_type.equals("image")) {
            if (groupMessageFireStore.sender_id.equals(activityGroupChatPage.dataUser.id)) {
                holder.me_image_hole_layout.visibility = View.VISIBLE
                holder.other_image_hole_layout.visibility = View.GONE
                holder.other_text_layout.visibility = View.GONE
                holder.me_text_layout.visibility = View.GONE
                Glide.with(context).load(activityGroupChatPage.dataUser.image).apply(helperMethods.profileRequestOption).into(holder.me_image_profile)
                holder.me_image_sender_name.text=activityGroupChatPage.dataUser.fname+" "+activityGroupChatPage.dataUser.lname

                if (activityGroupChatPage.groupItemData.creater_id.equals(groupMessageFireStore.sender_id)) {
                    holder.me_image_nectie.visibility = View.VISIBLE
                } else {
                    holder.me_image_nectie.visibility = View.GONE
                }
                Glide.with(context).load(groupMessageFireStore.message_content).apply(helperMethods.requestOption).into(holder.me_image)
                holder.me_image_time.text = helperMethods.getFormattedDate(groupMessageFireStore.send_time)
                holder.me_image_layout.setOnClickListener {
                    val intent = Intent(context, PhotoViewer::class.java)
                    intent.putExtra("name", "${activityGroupChatPage.dataUser.fname} ${activityGroupChatPage.dataUser.lname}")
                    intent.putExtra("image_url", groupMessageFireStore.message_content)
                    intent.putExtra("send_time", helperMethods.getFormattedDate(groupMessageFireStore.send_time))
                    context.startActivity(intent)
                }

                if (groupMessageFireStore.message_other_type.equals("forwarded")) {
                    holder.me_image_is_forward.visibility = View.VISIBLE
                } else {
                    holder.me_image_is_forward.visibility = View.GONE
                }
                setReplyMessage(groupMessageFireStore, holder.me_image_reply_layout, holder.me_image_reply_from, holder.me_image_reply_text, holder.me_image_reply_image_layout, holder.me_image_reply_image, holder.me_text_reply_view_line)
            } else {
                holder.other_image_hole_layout.visibility = View.VISIBLE
                holder.other_text_layout.visibility = View.GONE
                holder.me_text_layout.visibility = View.GONE
                holder.me_image_hole_layout.visibility = View.GONE
                Glide.with(context).load(activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.sender_id).image).apply(helperMethods.profileRequestOption).into(holder.other_image_profile)
                holder.other_image_sender_name.text=activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.sender_id).fname+" "+activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.sender_id).lname

                if (activityGroupChatPage.groupItemData.creater_id.equals(groupMessageFireStore.sender_id)) {
                    holder.other_image_nectie.visibility = View.VISIBLE
                } else {
                    holder.other_image_nectie.visibility = View.GONE
                }
                Glide.with(context).load(groupMessageFireStore.message_content).apply(helperMethods.requestOption).into(holder.other_image)
                holder.other_image_time.text = helperMethods.getFormattedDate(groupMessageFireStore.send_time)
                holder.other_image_layout.setOnClickListener {
                    val intent = Intent(context, PhotoViewer::class.java)
                    intent.putExtra("name", "${activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.sender_id).fname} ${activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.sender_id).lname}")
                    intent.putExtra("image_url", groupMessageFireStore.message_content)
                    intent.putExtra("send_time", helperMethods.getFormattedDate(groupMessageFireStore.send_time))
                    context.startActivity(intent)
                }
                if (groupMessageFireStore.message_other_type.equals("forwarded")) {
                    holder.other_image_is_forward.visibility = View.VISIBLE
                } else {
                    holder.other_image_is_forward.visibility = View.GONE
                }
                setReplyMessage(groupMessageFireStore, holder.other_image_reply_layout, holder.other_image_reply_from, holder.other_image_reply_text, holder.other_image_reply_image_layout, holder.other_image_reply_image, holder.other_text_reply_view_line)
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
            sendForwardMessage(groupMessageFireStore)
        }
        holder.other_forward_image.setOnClickListener {
            sendForwardMessage(groupMessageFireStore)
        }
        holder.me_forward_text.setOnClickListener {
            sendForwardMessage(groupMessageFireStore)
        }
        holder.me_forward_image.setOnClickListener {
            sendForwardMessage(groupMessageFireStore)
        }
    }

    fun sendForwardMessage(groupMessageFireStore: GroupMessageFireStore) {
        GlobalData.forwardType = groupMessageFireStore.message_type
        GlobalData.forwardContent = groupMessageFireStore.message_content
        activityGroupChatPage.finish()
        helperMethods.showToastMessage(context.getString(R.string.now_you_can_forward_the_message))
    }

    fun changeBackgroundColor(holder: GroupChatViewHolder) {
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

    fun setReplyMessage(groupMessageFireStore: GroupMessageFireStore, reply_layout: LinearLayout, reply_from: TextView, reply_text: TextView, reply_image_layout: LinearLayout, reply_image: ImageView, view_line: View) {
        //*********reply message*****
        if (groupMessageFireStore.inside_reply.containsKey("message_id") && !groupMessageFireStore.inside_reply.getValue("message_id").isEmpty()) {
            reply_layout.visibility = View.VISIBLE
            view_line.visibility = View.VISIBLE
            if (groupMessageFireStore.inside_reply.getValue("sender_id").equals(activityGroupChatPage.dataUser.id)) {
                reply_from.text = context.getString(R.string.you)
                reply_from.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else {
                reply_from.text = activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.inside_reply.getValue("sender_id")).fname + " " + activityGroupChatPage.findUserDetailsFromGroup(groupMessageFireStore.inside_reply.getValue("sender_id")).lname
                reply_from.setTextColor(ContextCompat.getColor(context, R.color.orange))
            }
            if (groupMessageFireStore.inside_reply.getValue("message_type").equals("text")) {
                reply_text.text = groupMessageFireStore.inside_reply.getValue("message_content")
                reply_text.visibility = View.VISIBLE
                reply_image_layout.visibility = View.GONE
            } else if (groupMessageFireStore.inside_reply.getValue("message_type").equals("image")) {
                Glide.with(context).load(groupMessageFireStore.inside_reply.getValue("message_content")).apply(helperMethods.requestOption).into(reply_image)
                reply_image_layout.visibility = View.VISIBLE
                reply_text.visibility = View.GONE
            }
            reply_layout.setOnClickListener {
                val position = groupMessageFireStore.inside_reply.getValue("position")
                (activityGroupChatPage.chat_recycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position.toInt(), 600)
                activityGroupChatPage.showChatScrollToBottom(position.toInt())
                val viewHolder: RecyclerView.ViewHolder? = activityGroupChatPage.chat_recycler.findViewHolderForAdapterPosition(position.toInt())
                if (viewHolder != null) {
                    changeBackgroundColor(viewHolder as GroupChatViewHolder)
                    Log.d("viewHoleder", "not null")
                } else {
                    replyPositionClicked = position
                    Log.d("viewHoleder", "is null")
                }
                viewHolder?.let {}
            }
        } else {
            reply_layout.visibility = View.GONE
            view_line.visibility = View.GONE
        }
    }
}
