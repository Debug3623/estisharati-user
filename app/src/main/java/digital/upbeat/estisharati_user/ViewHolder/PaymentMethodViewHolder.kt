package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.chat_item.view.*
import kotlinx.android.synthetic.main.payment_card_item.view.*

class PaymentMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val card_layout = itemView.card_layout
    val paypal_account_layout = itemView.paypal_account_layout
    val card_edit = itemView.card_edit
    val card_delete = itemView.card_delete
    val paypal_edit = itemView.paypal_edit
    val paypal_delete = itemView.paypal_delete
    val card_number = itemView.card_number
    val card_expiry_date = itemView.expiry_date
    val paypal_email = itemView.paypal_email
}