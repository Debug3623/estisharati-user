package estisharatibussiness.users.com.UserInterfaces

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_photo_viewer.*

class PhotoViewer : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_viewer)
        initViews()
        clickEvents()
        getDataFronIntent()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@PhotoViewer)
        helperMethods.setStatusBarColorLightIcon(this, R.color.black)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun getDataFronIntent() {
        val name = intent.getStringExtra("name")
        val image_url = intent.getStringExtra("image_url")
        val send_time = intent.getStringExtra("send_time")
        pv_name.text = name
        pv_send_time.text = send_time
        if (send_time.equals("")) {
            pv_send_time.visibility = View.GONE
        } else {
            pv_send_time.visibility = View.VISIBLE
        }
        pv_image.scaleType = ImageView.ScaleType.FIT_CENTER
        pv_image.isZoomEnabled = true
        Glide.with(this@PhotoViewer).asBitmap().apply(helperMethods.requestOption).load(image_url).into(object : CustomTarget<Bitmap?>() {
            override fun onLoadCleared(placeholder: Drawable?) {}
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                pv_image.setImageBitmap(resource)
            }
        })
    }
}