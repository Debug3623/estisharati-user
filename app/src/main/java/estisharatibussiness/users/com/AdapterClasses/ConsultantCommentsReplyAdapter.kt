package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.CourseDetails.Comment
import estisharatibussiness.users.com.FragmentClasses.Comments
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityConsultantDetails
import estisharatibussiness.users.com.PublicViewHolder.ConsultantCommentsReplyViewHolder

class ConsultantCommentsReplyAdapter(val context: Context, val comments: Comments?, val activityConsultantDetails: ActivityConsultantDetails?, val courseCommentsArrayList: ArrayList<Comment>, val consultantCommentsArrayList: ArrayList<estisharatibussiness.users.com.DataClassHelperMehtods.ConsultantDetails.Comment>) : RecyclerView.Adapter<ConsultantCommentsReplyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantCommentsReplyViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.comment_reply_item, parent, false)
        return ConsultantCommentsReplyViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return if (comments != null) {
            courseCommentsArrayList.size
        } else if (activityConsultantDetails != null) {
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
            holder.consultant_comments_reply_sub_recycler.adapter = ConsultantCommentsReplySubAdapter(context, comments, activityConsultantDetails, courseCommentsArrayList.get(position).replies, arrayListOf())
            holder.cmdMessage.setOnClickListener { comments.helperMethods.AlertPopup(courseCommentsArrayList.get(position).user.name, courseCommentsArrayList.get(position).comment) }
        } else if (activityConsultantDetails != null) {
            Glide.with(context).load(SharedPreferencesHelper(context).logInUser.image).apply(HelperMethods(context).profileRequestOption).into(holder.userImage)
            Glide.with(context).load(consultantCommentsArrayList.get(position).user.image_path).apply(HelperMethods(context).profileRequestOption).into(holder.cmdUserImage)
            holder.cmdUserName.text = consultantCommentsArrayList.get(position).user.name
            holder.cmdDateTime.text = consultantCommentsArrayList.get(position).created_at
            holder.cmdUserRating.rating = consultantCommentsArrayList.get(position).review.toFloat()
            holder.cmdReplyCount.text = consultantCommentsArrayList.get(position).replies.size.toString() + " " + context.getString(R.string.reply)
            holder.cmdMessage.text = consultantCommentsArrayList.get(position).comment
            holder.consultant_comments_reply_sub_recycler.adapter = ConsultantCommentsReplySubAdapter(context, comments, activityConsultantDetails, arrayListOf(), consultantCommentsArrayList.get(position).replies)
            holder.cmdMessage.setOnClickListener { activityConsultantDetails.helperMethods.AlertPopup(consultantCommentsArrayList.get(position).user.name, consultantCommentsArrayList.get(position).comment) }

        }




        holder.submitReplay.setOnClickListener {
            if (!holder.commentsReply.text.toString().equals("")) {
                if (comments != null) {
                    comments.courseCommentApiCall(courseCommentsArrayList.get(position).course_id, courseCommentsArrayList.get(position).id, holder.commentsReply.text.toString())
                } else if (activityConsultantDetails != null) {
                    activityConsultantDetails.consultantCommentApiCall(consultantCommentsArrayList.get(position).consultant_id, consultantCommentsArrayList.get(position).id, holder.commentsReply.text.toString())
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
