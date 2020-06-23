package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val other_text_layout = itemView.other_text_layout
    val other_text_profile = itemView.other_text_profile
    val other_text = itemView.other_text
    val other_text_time = itemView.other_text_time

    val other_image_hole_layout = itemView.other_image_hole_layout
    val other_image_profile = itemView.other_image_profile
    val other_image_layout = itemView.other_image_layout
    val other_image = itemView.other_image
    val other_image_time = itemView.other_image_time

    val me_text_layout = itemView.me_text_layout
    val me_text = itemView.me_text
    val me_text_time = itemView.me_text_time
    val me_text_profile = itemView.me_text_profile

    val me_image_layout_hole = itemView.me_image_layout_hole
    val me_image_layout = itemView.me_image_layout
    val me_image = itemView.me_image
    val me_image_time=itemView.me_image_time
    val me_image_profile=itemView.me_image_profile
}