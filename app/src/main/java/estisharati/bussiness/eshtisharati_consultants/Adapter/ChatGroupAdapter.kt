package estisharati.bussiness.eshtisharati_consultants.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharati.bussiness.eshtisharati_consultants.DataClassHelper.Group.GroupItem
import estisharati.bussiness.eshtisharati_consultants.Fragment.Subscribers
import estisharati.bussiness.eshtisharati_consultants.Helper.GlobalData
import estisharati.bussiness.eshtisharati_consultants.Helper.HelperMethods
import estisharati.bussiness.eshtisharati_consultants.R
import estisharati.bussiness.eshtisharati_consultants.UI.GroupChatPage
import estisharati.bussiness.eshtisharati_consultants.ViewHolder.ChatGroupViewHolder
import kotlinx.android.synthetic.main.group_item.view.*

class ChatGroupAdapter(val context: Context, val subscribers: Subscribers, val groupArrayList: ArrayList<GroupItem>) : RecyclerView.Adapter<ChatGroupViewHolder>() {
    val helperMethods: HelperMethods

    init {
        helperMethods = HelperMethods(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatGroupViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false)
        return ChatGroupViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return groupArrayList.size
    }

    override fun onBindViewHolder(holder: ChatGroupViewHolder, position: Int) {
        Glide.with(context).load(groupArrayList.get(position).group_image).apply(subscribers.helperMethods.requestOption).into(holder.groupIcon)
        holder.groupName.text = groupArrayList.get(position).group_name
        holder.groupMembers.text = groupArrayList.get(position).group_members.size.toString() + " " + context.getString(R.string.members)
        holder.groupIcon.setOnClickListener {
            subscribers.createGroupPopup(groupArrayList.get(position).group_id)
        }
        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, GroupChatPage::class.java)
            intent.putExtra("group_id", groupArrayList.get(position).group_id)
            intent.putExtra("forward_type", GlobalData.forwardType)
            intent.putExtra("forward_content", GlobalData.forwardContent)
            context.startActivity(intent)
            GlobalData.forwardType = ""
            GlobalData.forwardContent = ""
        }
    }
}
