package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_about_us.*

class AboutUs : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@AboutUs)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }
}
