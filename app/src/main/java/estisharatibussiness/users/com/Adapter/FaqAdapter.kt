package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.DataClassHelper.FaqDetails.Data
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.ViewHolder.FaqViewHolder

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
            }else{
                holder.faq_des.visibility = View.VISIBLE
                holder.faq_arrow.rotation = 90f
            }
        }
    }

    override fun getItemCount(): Int {
        return faqArrayList.size
    }
}