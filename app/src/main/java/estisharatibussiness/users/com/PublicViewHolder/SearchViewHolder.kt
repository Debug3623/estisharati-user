package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_item.view.*

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val parentLayout = itemView.parentLayout
    val image = itemView.image
    val name = itemView.name
}