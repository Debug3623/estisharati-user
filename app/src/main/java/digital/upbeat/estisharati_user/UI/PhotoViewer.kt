package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_photo_viewer.*

class PhotoViewer : AppCompatActivity() {
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
        intent.extras?.let {
            val name = it.getString("name")
            val image_url = it.getString("image_url")
            val send_time = it.getString("send_time")
            pv_name.text = name
            pv_send_time.text =send_time
            Glide.with(this@PhotoViewer).load(image_url).apply(helperMethods.requestOption).into(pv_image)
        }
    }
}