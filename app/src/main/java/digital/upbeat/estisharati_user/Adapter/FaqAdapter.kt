package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.FaqDetails.Data
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.ViewHolder.FaqViewHolder

class FaqAdapter(val context: Context, val faqArrayList: ArrayList<Data>) : RecyclerView.Adapter<FaqViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.faq_recycler_row, parent, false)
        return FaqViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.faq_title.text = faqArrayList[position].title
        holder.faq_des.text = faqArrayList[position].description

        holder.faq_parentLayout.setOnClickListener {
            if (holder.faq_des.visibility == View.VISIBLE) {
                holder.faq_des.visibility = View.GONE
                holder.faq_arrow.rotation = 0f
                //holder.faq_arrow.setImageResource(R.drawable.ic_right_arrow_black)
            }else{
                holder.faq_des.visibility = View.VISIBLE
                holder.faq_arrow.rotation = 90f
               // holder.faq_arrow.setImageResource(R.drawable.ic_down_arrow_black)
            }
        }
    }

    override fun getItemCount(): Int {
        return faqArrayList.size
    }
}