package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.CourseDetails.Reply
import estisharatibussiness.users.com.FragmentClasses.Comments
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityConsultantDetails
import estisharatibussiness.users.com.PublicViewHolder.ConsultantCommentsSubReplyViewHolder

class ConsultantCommentsReplySubAdapter(val context: Context, val comments: Comments?, val activityConsultantDetails: ActivityConsultantDetails?, val courseRepliesArrayList: ArrayList<Reply>, val consultantRepliesArrayList: ArrayList<estisharatibussiness.users.com.DataClassHelperMehtods.ConsultantDetails.Reply>) : RecyclerView.Adapter<ConsultantCommentsSubReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantCommentsSubReplyViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.comments_reply_sub_item, parent, false)
        return ConsultantCommentsSubReplyViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return if (comments != null) {
            courseRepliesArrayList.size
        } else if (activityConsultantDetails != null) {
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
            holder.nectie.visibility = if (comments.activityCourseDetails.findConsultantID(courseRepliesArrayList.get(position).user.id)) View.VISIBLE else View.GONE
        } else if (activityConsultantDetails != null) {
            Glide.with(context).load(consultantRepliesArrayList.get(position).user.image_path).apply(HelperMethods(context).profileRequestOption).into(holder.subReplyUserImage)
            holder.subReplyUserName.text = consultantRepliesArrayList.get(position).user.name
            holder.subReplyDateTime.text = consultantRepliesArrayList.get(position).created_at
            holder.subReplyMessage.text = consultantRepliesArrayList.get(position).comment
            holder.subReplyMessage.setOnClickListener {
                activityConsultantDetails.helperMethods.AlertPopup(consultantRepliesArrayList.get(position).user.name, consultantRepliesArrayList.get(position).comment)
            }
            holder.nectie.visibility = if (activityConsultantDetails.consultantDetailsResponse.id.equals(consultantRepliesArrayList.get(position).user.id)) View.VISIBLE else View.GONE
        }
    }
}
