package digital.upbeat.estisharati_consultant.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.R
import kotlinx.android.synthetic.main.activity_help.*

class Help : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Help)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        call_us.setOnClickListener {
            helperMethods.actionDialIntent(call_us.text.toString())
        }
    }
}
