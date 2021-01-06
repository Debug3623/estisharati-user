package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.CourseDetails.Comment
import digital.upbeat.estisharati_user.Fragment.Comments
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.ViewHolder.ConsultantCommentsReplyViewHolder

class ConsultantCommentsReplyAdapter(val context: Context, val comments: Comments?, val consultantDetails: ConsultantDetails?, val courseCommentsArrayList: ArrayList<Comment>, val consultantCommentsArrayList: ArrayList<digital.upbeat.estisharati_user.DataClassHelper.ConsultantDetails.Comment>) : RecyclerView.Adapter<ConsultantCommentsReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantCommentsReplyViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.comment_reply_item, parent, false)
        return ConsultantCommentsReplyViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return if (comments != null) {
            courseCommentsArrayList.size
        } else if (consultantDetails != null) {
            consultantCommentsArrayList.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: ConsultantCommentsReplyViewHolder, position: Int) {
        holder.consultant_comments_reply_sub_recycler.setHasFixedSize(true)
        holder.consultant_comments_reply_sub_recycler.removeAllViews()
        holder.consultant_comments_reply_sub_recycler.layoutManager = LinearLayoutManager(context)

        if (comments != null) {
            Glide.with(context).load(SharedPreferencesHelper(context).logInUser.image).apply(HelperMethods(context).profileRequestOption).into(holder.userImage)
            Glide.with(context).load(courseCommentsArrayList.get(position).user.image_path).apply(HelperMethods(context).profileRequestOption).into(holder.cmdUserImage)
            holder.cmdUserName.text = courseCommentsArrayList.get(position).user.name
            holder.cmdDateTime.text = courseCommentsArrayList.get(position).created_at
            holder.cmdUserRating.rating = courseCommentsArrayList.get(position).review.toFloat()
            holder.cmdReplyCount.text = courseCommentsArrayList.get(position).replies.size.toString() + " " + context.getString(R.string.reply)
            holder.cmdMessage.text = courseCommentsArrayList.get(position).comment
            holder.consultant_comments_reply_sub_recycler.adapter = ConsultantCommentsReplySubAdapter(context, comments, consultantDetails, courseCommentsArrayList.get(position).replies, arrayListOf())
            holder.cmdMessage.setOnClickListener { comments.helperMethods.AlertPopup(courseCommentsArrayList.get(position).user.name, courseCommentsArrayList.get(position).comment) }
        } else if (consultantDetails != null) {
            Glide.with(context).load(SharedPreferencesHelper(context).logInUser.image).apply(HelperMethods(context).profileRequestOption).into(holder.userImage)
            Glide.with(context).load(consultantCommentsArrayList.get(position).user.image_path).apply(HelperMethods(context).profileRequestOption).into(holder.cmdUserImage)
            holder.cmdUserName.text = consultantCommentsArrayList.get(position).user.name
            holder.cmdDateTime.text = consultantCommentsArrayList.get(position).created_at
            holder.cmdUserRating.rating = consultantCommentsArrayList.get(position).review.toFloat()
            holder.cmdReplyCount.text = consultantCommentsArrayList.get(position).replies.size.toString() + " " + context.getString(R.string.reply)
            holder.cmdMessage.text = consultantCommentsArrayList.get(position).comment
            holder.consultant_comments_reply_sub_recycler.adapter = ConsultantCommentsReplySubAdapter(context, comments, consultantDetails, arrayListOf(), consultantCommentsArrayList.get(position).replies)
            holder.cmdMessage.setOnClickListener { consultantDetails.helperMethods.AlertPopup(consultantCommentsArrayList.get(position).user.name, consultantCommentsArrayList.get(position).comment) }

        }




        holder.submitReplay.setOnClickListener {
            if (!holder.commentsReply.text.toString().equals("")) {
                if (comments != null) {
                    comments.courseCommentApiCall(courseCommentsArrayList.get(position).course_id, courseCommentsArrayList.get(position).id, holder.commentsReply.text.toString())
                } else if (consultantDetails != null) {
                    consultantDetails.consultantCommentApiCall(consultantCommentsArrayList.get(position).consultant_id, consultantCommentsArrayList.get(position).id, holder.commentsReply.text.toString())
                }
            } else {
                Toast.makeText(context, context.getString(R.string.please_enter_your_reply), Toast.LENGTH_LONG).show()
            }
        }

        holder.sub_item_reply_layout.visibility = View.GONE
        holder.main_item.setOnClickListener {
            if (holder.sub_item_reply_layout.visibility == View.VISIBLE) {
                holder.sub_item_reply_layout.visibility = View.GONE
            } else {
                holder.sub_item_reply_layout.visibility = View.VISIBLE
            }
        }
    }
}
