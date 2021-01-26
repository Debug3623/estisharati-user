package digital.upbeat.estisharati_consultant.UI

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.Utils.BaseCompatActivity
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