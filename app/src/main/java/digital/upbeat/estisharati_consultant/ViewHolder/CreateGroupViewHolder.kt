package digital.upbeat.estisharati_consultant.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_user_item.view.*

class CreateGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
   val profile_picture=itemView.profile_picture
   val user_name=itemView.user_name
   val tick=itemView.tick
   val parent_layout=itemView.parent_layout
}