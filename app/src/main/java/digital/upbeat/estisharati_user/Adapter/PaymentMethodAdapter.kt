package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.PaymentMethodList.Data
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import digital.upbeat.estisharati_user.ViewHolder.*

class PaymentMethodAdapter(val context: Context, val paymentMethods: PaymentMethods, val paymentMethodsArrayList: ArrayList<Data>) : RecyclerView.Adapter<PaymentMethodViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.payment_card_item, parent, false)
        return PaymentMethodViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return paymentMethodsArrayList.size
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.card_number.text = paymentMethodsArrayList.get(position).details?.cardno
        holder.card_expiry_date.text = paymentMethodsArrayList.get(position).details?.expiry
        holder.paypal_email.text = paymentMethodsArrayList.get(position).details?.email
        if (paymentMethodsArrayList.get(position).payment_method.equals("1")) {
            holder.paypal_account_layout.visibility = View.GONE
            holder.card_layout.visibility = View.VISIBLE
        } else if (paymentMethodsArrayList.get(position).payment_method.equals("2")) {
            holder.paypal_account_layout.visibility = View.VISIBLE
            holder.card_layout.visibility = View.GONE
        }
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
            intent.putExtra("payment_mehtod", paymentMethodsArrayList.get(position))
            context.startActivity(intent)
        }
        holder.card_delete.setOnClickListener {
            val cardNumber = paymentMethodsArrayList.get(position).details?.cardno
            paymentMethods.cardAccountRemovePopup(paymentMethodsArrayList.get(position), "Are you sure?\nDo you want to remove the card ends in " + cardNumber?.subSequence((cardNumber.length - 4), cardNumber.length))
        }
        holder.paypal_edit.setOnClickListener {
            val intent = Intent(context, AddPaymentMethod::class.java)
            intent.putExtra("click_from", "update")
            intent.putExtra("card_paypal", "paypal")
            intent.putExtra("payment_mehtod", paymentMethodsArrayList.get(position))

            context.startActivity(intent)
        }
        holder.paypal_delete.setOnClickListener {
            paymentMethods.cardAccountRemovePopup(paymentMethodsArrayList.get(position), "Are you sure?\nDo you want to remove the paypal account " + paymentMethodsArrayList.get(position).details?.email)
        }
    }
}
