package estisharatibussiness.users.com.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.favorite_consultant_item.view.*

class FavoriteConsultantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val consultantImage=itemView.consultantImage
    val consultantName=itemView.consultantName
    val consultantJobTitle=itemView.consultantJobTitle
    val consultantRate=itemView.consultantRate
    val consultantPrice=itemView.consultantPrice
    val favoriteIcon=itemView.favoriteIcon
    val parentLayout=itemView.parentLayout
    val onlineStatus=itemView.onlineStatus
}