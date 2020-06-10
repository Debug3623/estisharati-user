package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import digital.upbeat.estisharati_user.ViewHolder.*

class PaymentMethodAdapter(val context: Context, val paymentMethods: PaymentMethods, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<PaymentMethodViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.payment_card_item, parent, false)
        return PaymentMethodViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.card_layout.setOnClickListener {
            holder.card_layout.setBackgroundResource(R.drawable.corner_redius_10dp_white_stroke_green)
        }
        holder.paypal_account_layout.setOnClickListener {
            holder.paypal_account_layout.setBackgroundResource(R.drawable.corner_redius_10dp_white_stroke_green)
        }
        holder.card_edit.setOnClickListener {
            val intent = Intent(context, AddPaymentMethod::class.java)
            intent.putExtra("click_from", "update")
            intent.putExtra("card_paypal", "card")
            context.startActivity(intent)
        }
        holder.card_delete.setOnClickListener {
            paymentMethods.cardAccountRemovePopup("Are you sure?\nDo you want to remove the card ends in 5967")
        }
        holder.paypal_edit.setOnClickListener {
            val intent = Intent(context, AddPaymentMethod::class.java)
            intent.putExtra("click_from", "update")
            intent.putExtra("card_paypal", "paypal")
            context.startActivity(intent)
        }
        holder.paypal_delete.setOnClickListener {
            paymentMethods.cardAccountRemovePopup("Are you sure?\nDo you want to remove the paypal account Margap_ali@gmail.com")
        }
        if (position % 2 == 0) {
            Log.d("position_payment", "add" + position)
            holder.card_layout.visibility = View.VISIBLE
            holder.paypal_account_layout.visibility = View.GONE
        } else {
            Log.d("position_payment", "even" + position)
            holder.card_layout.visibility = View.GONE
            holder.paypal_account_layout.visibility = View.VISIBLE
        }
    }
}
