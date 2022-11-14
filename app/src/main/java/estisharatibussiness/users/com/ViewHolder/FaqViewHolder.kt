package estisharatibussiness.users.com.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.faq_recycler_row.view.*

class FaqViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    val faq_title = itemView.faq_title
    val faq_des = itemView.faq_des
    val faq_arrow = itemView.faq_arrow
    val faq_parentLayout = itemView.faq_parentLayout
}