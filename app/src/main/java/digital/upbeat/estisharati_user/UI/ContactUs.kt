package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_contact_us.*

class ContactUs : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ContactUs)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }
}
