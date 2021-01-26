package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Blog.Data
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_blog_details.*

class BlogDetails : AppCompatActivity() {
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
        helperMethods = HelperMethods(this@BlogDetails)
        val blogArrayList = intent.getParcelableArrayListExtra<Data>("blogArrayList") as ArrayList<Data>
        blogItemDetails = blogArrayList.get(intent.getIntExtra("position", 0))
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun setDetails() {
        blogTitle.text = blogItemDetails.title
        Glide.with(this@BlogDetails).load(blogItemDetails.image_path).apply(helperMethods.requestOption).into(blogImage)
        blogContent.text = helperMethods.getHtmlText(blogItemDetails.description)
    }
}