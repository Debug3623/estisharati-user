package estisharatibussiness.users.com.UserInterfaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.gson.Gson
import estisharatibussiness.users.com.DataClassHelperMehtods.Blog.Data
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import kotlinx.android.synthetic.main.activity_blog_details.*

class ActivityBlogDetails : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var blogItemDetails: Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_details)
        initViews()
        clickEvents()
        setDetails()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityBlogDetails)
        blogItemDetails = Gson().fromJson(intent.getStringExtra("blogDetailsData"), Data::class.java)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun setDetails() {
        blogTitle.text = blogItemDetails.title
        Glide.with(this@ActivityBlogDetails).load(blogItemDetails.image_path).apply(helperMethods.requestOption).into(blogImage)
        blogContent.text = helperMethods.getHtmlText(blogItemDetails.description)
    }
}