package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.course_content_item.view.*

class CourseContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val child_layout=itemView.child_layout
    val expand_icon=itemView.expand_icon
    val main_text=itemView.main_text
    val course_content_sub_recycler=itemView.course_content_sub_recycler
}