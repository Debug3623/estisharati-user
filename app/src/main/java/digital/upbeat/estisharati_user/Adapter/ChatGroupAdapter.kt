package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Group.GroupItem
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ChatHome
import digital.upbeat.estisharati_user.UI.GroupChatPage
import digital.upbeat.estisharati_user.ViewHolder.ChatGroupViewHolder

class ChatGroupAdapter(val context: Context, val chatHome: ChatHome, val groupArrayList: ArrayList<GroupItem>) : RecyclerView.Adapter<ChatGroupViewHolder>() {
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
        Glide.with(context).load(groupArrayList.get(position).group_image).apply(chatHome.helperMethods.requestOption).into(holder.groupIcon)
        holder.groupName.text = groupArrayList.get(position).group_name
        holder.groupMembers.text = groupArrayList.get(position).group_members.size.toString() + " " + context.getString(R.string.members)

        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, GroupChatPage::class.java)
            intent.putExtra("group_id", groupArrayList.get(position).group_id)
            intent.putExtra("forward_type", GlobalData.forwardType)
            intent.putExtra("forward_content", GlobalData.forwardContent)
            context.startActivity(intent)
        }
    }
}
