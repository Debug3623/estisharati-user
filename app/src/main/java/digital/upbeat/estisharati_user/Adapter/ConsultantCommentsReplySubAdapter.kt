package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.ViewHolder.ConsultantCommentsReplyViewHolder
import digital.upbeat.estisharati_user.ViewHolder.ConsultantCommentsSubReplyViewHolder

class ConsultantCommentsReplySubAdapter(val context: Context,val consultantDetails: ConsultantDetails, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<ConsultantCommentsSubReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantCommentsSubReplyViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.comments_reply_sub_item, parent, false)
        return ConsultantCommentsSubReplyViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: ConsultantCommentsSubReplyViewHolder, position: Int) {

    }
}
