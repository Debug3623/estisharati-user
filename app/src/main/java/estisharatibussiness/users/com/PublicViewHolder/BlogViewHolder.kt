package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.blog_item.view.*

class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val blogItemLayout = itemView.blogItemLayout
    val blogTitle = itemView.blogTitle
    val blogImage = itemView.blogImage
    val blogContent = itemView.blogContent
    val divider = itemView.divider
}