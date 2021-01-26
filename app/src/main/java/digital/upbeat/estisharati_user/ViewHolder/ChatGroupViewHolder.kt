package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item.view.*

class ChatGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val groupIcon = itemView.groupIcon
    val groupName = itemView.groupName
    val groupMembers = itemView.groupMembers
    val parentLayout = itemView.parentLayout
}