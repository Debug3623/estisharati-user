package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.ViewHolder.ConsultantCommentsReplyViewHolder

class ConsultantCommentsReplyAdapter(val context: Context,val consultantDetails: ConsultantDetails, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<ConsultantCommentsReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantCommentsReplyViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.comment_reply_item, parent, false)
        return ConsultantCommentsReplyViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: ConsultantCommentsReplyViewHolder, position: Int) {
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")



        holder.consultant_comments_reply_sub_recycler.setHasFixedSize(true)
        holder.consultant_comments_reply_sub_recycler.removeAllViews()
        holder.consultant_comments_reply_sub_recycler.layoutManager = LinearLayoutManager(context)
        holder.consultant_comments_reply_sub_recycler.adapter = ConsultantCommentsReplySubAdapter(context, consultantDetails, arrayList)
    }

}
