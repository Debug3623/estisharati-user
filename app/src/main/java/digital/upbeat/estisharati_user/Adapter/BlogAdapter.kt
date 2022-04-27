package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import digital.upbeat.estisharati_user.DataClassHelper.Blog.Data
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.Blog
import digital.upbeat.estisharati_user.UI.BlogDetails
import digital.upbeat.estisharati_user.ViewHolder.BlogViewHolder

class BlogAdapter(val context: Context, val blog: Blog, var blogArrayList: ArrayList<Data>) : RecyclerView.Adapter<BlogViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.blog_item, parent, false)
        return BlogViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return blogArrayList.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.blogTitle.text = blogArrayList.get(position).title
        Glide.with(context).load(blogArrayList.get(position).image_path).apply(blog.helperMethods.requestOption).into(holder.blogImage)
        holder.blogContent.text = blog.helperMethods.getHtmlText(blogArrayList.get(position).description)

        holder.blogItemLayout.setOnClickListener {
            val intent =Intent(context,BlogDetails::class.java)
            val blogDetailsData=Gson().toJson(blogArrayList[position]).toString()
            intent.putExtra("blogDetailsData",blogDetailsData)
            context.startActivity(intent)
        }
    }
}
