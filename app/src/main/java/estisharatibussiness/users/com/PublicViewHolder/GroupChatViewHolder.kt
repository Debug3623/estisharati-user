package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_chat_item.view.*

class GroupChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val parent_layout=itemView.parent_layout

    val other_text_layout = itemView.other_text_layout
    val other_text_profile = itemView.other_text_profile
    val other_text_nectie = itemView.other_text_nectie
    val other_text = itemView.other_text
    val other_text_time = itemView.other_text_time
    val other_forward_text=itemView.other_forward_text
    val other_text_is_forward=itemView.other_text_is_forward
    val other_text_sender_name=itemView.other_text_sender_name

    //***************

    val other_image_hole_layout = itemView.other_image_hole_layout
    val other_image_profile = itemView.other_image_profile
    val other_image_nectie = itemView.other_image_nectie
    val other_image_layout = itemView.other_image_layout
    val other_image = itemView.other_image
    val other_image_time = itemView.other_image_time
    val other_forward_image = itemView.other_forward_image
    val other_image_is_forward=itemView.other_image_is_forward
    val other_image_sender_name=itemView.other_image_sender_name

    //******************
    val me_text_layout = itemView.me_text_layout
    val me_text = itemView.me_text
    val me_text_time = itemView.me_text_time
    val me_text_profile = itemView.me_text_profile
    val me_text_nectie = itemView.me_text_nectie
    val me_forward_text = itemView.me_forward_text
    val me_text_is_forward=itemView.me_text_is_forward
    val me_text_sender_name=itemView.me_text_sender_name

    //******************

    val me_image_hole_layout = itemView.me_image_hole_layout
    val me_image_layout = itemView.me_image_layout
    val me_image = itemView.me_image
    val me_image_time=itemView.me_image_time
    val me_image_profile=itemView.me_image_profile
    val me_image_nectie=itemView.me_image_nectie
    val me_forward_image=itemView.me_forward_image
    val me_image_is_forward=itemView.me_image_is_forward
    val me_image_sender_name=itemView.me_image_sender_name

    //******************


    val other_text_reply_layout=itemView.other_text_reply_layout
    val other_text_reply_from=itemView.other_text_reply_from
    val other_text_reply_text=itemView.other_text_reply_text
    val other_text_reply_image_layout=itemView.other_text_reply_image_layout
    val other_text_reply_image=itemView.other_text_reply_image
    val other_text_reply_view_line=itemView.other_text_reply_view_line
    //******************

    val other_image_reply_layout=itemView.other_image_reply_layout
    val other_image_reply_from=itemView.other_image_reply_from
    val other_image_reply_text=itemView.other_image_reply_text
    val other_image_reply_image_layout=itemView.other_image_reply_image_layout
    val other_image_reply_image=itemView.other_image_reply_image

    //******************

    val me_text_reply_layout=itemView.me_text_reply_layout
    val me_text_reply_from=itemView.me_text_reply_from
    val me_text_reply_text=itemView.me_text_reply_text
    val me_text_reply_image_layout=itemView.me_text_reply_image_layout
    val me_text_reply_image=itemView.me_text_reply_image
    val me_text_reply_view_line=itemView.me_text_reply_view_line
    //******************

    val me_image_reply_layout=itemView.me_image_reply_layout
    val me_image_reply_from=itemView.me_image_reply_from
    val me_image_reply_text=itemView.me_image_reply_text
    val me_image_reply_image_layout=itemView.me_image_reply_image_layout
    val me_image_reply_image=itemView.me_image_reply_image



}