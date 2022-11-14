package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelper.Group.GroupItem
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.ChatHomeActivity
import estisharatibussiness.users.com.UserInterface.ActivityGroupChatPage
import estisharatibussiness.users.com.ViewHolder.ChatGroupViewHolder

class ChatGroupAdapter(val context: Context, val chatHomeActivity: ChatHomeActivity, val groupArrayList: ArrayList<GroupItem>) : RecyclerView.Adapter<ChatGroupViewHolder>() {
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
        Glide.with(context).load(groupArrayList.get(position).group_image).apply(chatHomeActivity.helperMethods.requestOption).into(holder.groupIcon)
        holder.groupName.text = groupArrayList.get(position).group_name
        holder.groupMembers.text = groupArrayList.get(position).group_members.size.toString() + " " + context.getString(R.string.members)

        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, ActivityGroupChatPage::class.java)
            intent.putExtra("group_id", groupArrayList.get(position).group_id)
            intent.putExtra("forward_type", GlobalData.forwardType)
            intent.putExtra("forward_content", GlobalData.forwardContent)
            context.startActivity(intent)
        }
    }
}
