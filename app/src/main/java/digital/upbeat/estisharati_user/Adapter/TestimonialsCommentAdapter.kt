package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Blog.Data
import digital.upbeat.estisharati_user.DataClassHelper.TestimonialsDetails.Comment
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.Blog
import digital.upbeat.estisharati_user.UI.BlogDetails
import digital.upbeat.estisharati_user.UI.TestimonialsDetails
import digital.upbeat.estisharati_user.ViewHolder.BlogViewHolder
import digital.upbeat.estisharati_user.ViewHolder.TestimonialsCommentViewHolder

class TestimonialsCommentAdapter(val context: Context, val testimonialsDetails: TestimonialsDetails, var testimonialsCommentArrayList: ArrayList<Comment>) : RecyclerView.Adapter<TestimonialsCommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestimonialsCommentViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.testimonials_comment_item, parent, false)
        return TestimonialsCommentViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return testimonialsCommentArrayList.size
    }

    override fun onBindViewHolder(holder: TestimonialsCommentViewHolder, position: Int) {
        Glide.with(context).load(testimonialsCommentArrayList.get(position).user.image_path).apply(testimonialsDetails.helperMethods.requestOption).into(holder.profilePicture)
        holder.userName.text = testimonialsCommentArrayList.get(position).user.name
        holder.createdAt.text = testimonialsCommentArrayList.get(position).created_at
        holder.comment.text = testimonialsCommentArrayList.get(position).comment
    }
}
