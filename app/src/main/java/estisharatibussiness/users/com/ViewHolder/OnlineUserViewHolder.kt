package estisharatibussiness.users.com.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.online_user_item.view.*

class OnlineUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val user_layout = itemView.user_layout
    val all_btn = itemView.all_btn
    val profile_picture = itemView.profile_picture
    val nectie = itemView.nectie
    val online_status = itemView.online_status
    val name = itemView.name
}