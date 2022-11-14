package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelper.TestimonialsDetails.Comment
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.TestimonialsDetails
import estisharatibussiness.users.com.ViewHolder.TestimonialsCommentViewHolder

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
