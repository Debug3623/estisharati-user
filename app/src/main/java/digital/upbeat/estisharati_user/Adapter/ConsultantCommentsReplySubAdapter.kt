package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.CourseDetails.Reply
import digital.upbeat.estisharati_user.Fragment.Comments
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.ViewHolder.ConsultantCommentsSubReplyViewHolder

class ConsultantCommentsReplySubAdapter(val context: Context, val comments: Comments?, val consultantDetails: ConsultantDetails?, val courseRepliesArrayList: ArrayList<Reply>, val consultantRepliesArrayList: ArrayList<digital.upbeat.estisharati_user.DataClassHelper.ConsultantDetails.Reply>) : RecyclerView.Adapter<ConsultantCommentsSubReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantCommentsSubReplyViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.comments_reply_sub_item, parent, false)
        return ConsultantCommentsSubReplyViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return if (comments != null) {
            courseRepliesArrayList.size
        } else if (consultantDetails != null) {
            consultantRepliesArrayList.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: ConsultantCommentsSubReplyViewHolder, position: Int) {
        if (comments != null) {
            Glide.with(context).load(courseRepliesArrayList.get(position).user.image_path).apply(HelperMethods(context).profileRequestOption).into(holder.subReplyUserImage)
            holder.subReplyUserName.text = courseRepliesArrayList.get(position).user.name
            holder.subReplyDateTime.text = courseRepliesArrayList.get(position).created_at
            holder.subReplyMessage.text = courseRepliesArrayList.get(position).comment
            holder.subReplyMessage.setOnClickListener {
                comments.helperMethods.AlertPopup(courseRepliesArrayList.get(position).user.name, courseRepliesArrayList.get(position).comment)
            }
        } else if (consultantDetails != null) {
            Glide.with(context).load(consultantRepliesArrayList.get(position).user.image_path).apply(HelperMethods(context).profileRequestOption).into(holder.subReplyUserImage)
            holder.subReplyUserName.text = consultantRepliesArrayList.get(position).user.name
            holder.subReplyDateTime.text = consultantRepliesArrayList.get(position).created_at
            holder.subReplyMessage.text = consultantRepliesArrayList.get(position).comment
            holder.subReplyMessage.setOnClickListener {
                consultantDetails.helperMethods.AlertPopup(consultantRepliesArrayList.get(position).user.name, consultantRepliesArrayList.get(position).comment)
            }
        }
    }
}
