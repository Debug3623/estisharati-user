package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import estisharatibussiness.users.com.DataClassHelperMehtods.Blog.Data
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityBlogActivity
import estisharatibussiness.users.com.UserInterfaces.ActivityBlogDetails
import estisharatibussiness.users.com.PublicViewHolder.BlogViewHolder

class BlogAdapter(val context: Context, val activityBlogActivity: ActivityBlogActivity, var blogArrayList: ArrayList<Data>) : RecyclerView.Adapter<BlogViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.blog_item, parent, false)
        return BlogViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return blogArrayList.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.blogTitle.text = blogArrayList.get(position).title
        Glide.with(context).load(blogArrayList.get(position).image_path).apply(activityBlogActivity.helperMethods.requestOption).into(holder.blogImage)
        holder.blogContent.text = activityBlogActivity.helperMethods.getHtmlText(blogArrayList.get(position).description)

        holder.blogItemLayout.setOnClickListener {
            val intent =Intent(context,ActivityBlogDetails::class.java)
            val blogDetailsData=Gson().toJson(blogArrayList[position]).toString()
            intent.putExtra("blogDetailsData",blogDetailsData)
            context.startActivity(intent)
        }
    }
}
