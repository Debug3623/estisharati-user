package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelper.posts.Data
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.PostDetailsActivity
import estisharatibussiness.users.com.UserInterface.PostsActivity
import estisharatibussiness.users.com.ViewHolder.TestimonialsViewHolder

class PostsAdapter(val context: Context, val testimonials: PostsActivity, var postsArrayList: ArrayList<Data>) : RecyclerView.Adapter<TestimonialsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestimonialsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.testimonials_item, parent, false)
        return TestimonialsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return postsArrayList.size
    }

    override fun onBindViewHolder(holder: TestimonialsViewHolder, position: Int) {
        Glide.with(context).load(postsArrayList.get(position).user.image).apply(testimonials.helperMethods.requestOption).into(holder.profilePicture)
        holder.userName.text = postsArrayList.get(position).user.name
        holder.commentsCount.text = postsArrayList.get(position).comments_count.toString() + " " + context.getString(R.string.comments) //        if (testimonialsArrayList.get(position).type.equals("consultant")) {
        //            holder.testimonialType.text = testimonialsArrayList.get(position).service_name + " ( " + testimonialsArrayList.get(position).consultant_category + " )"
        //        } else {
        //            holder.testimonialType.text = testimonialsArrayList.get(position).service_name
        //        }
        holder.testimonialsContent.text = postsArrayList.get(position).content

        holder.holeLayout.setOnClickListener {
            val intent = Intent(context, PostDetailsActivity::class.java)
            intent.putExtra("postId", postsArrayList[position].id)
            context.startActivity(intent)
        }
    }
}
