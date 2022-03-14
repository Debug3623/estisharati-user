package digital.upbeat.estisharati_user.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import digital.upbeat.estisharati_user.DataClassHelper.PackagesOptions.PackagesOptions
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_thanks_page.*

class ThanksPage : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thanks_page)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ThanksPage)
        chooseName.text = GlobalData.packagesOptions.name
        when (GlobalData.packagesOptions.type) {
            "consultation" -> {
                go_to_home_page.text = getString(R.string.my_consultations)
            }
            "course" -> {
                go_to_home_page.text = getString(R.string.my_courses)
            }
            "subscription" -> {
                go_to_home_page.text = getString(R.string.my_packages)
            }
        }
    }

    fun clickEvents() {
        go_to_home_page.setOnClickListener {
            val intent = Intent(this@ThanksPage, UserDrawer::class.java)
            when (GlobalData.packagesOptions.type) {
                "consultation" -> {
                    intent.putExtra("type", "consultation")
                }
                "course" -> {
                    intent.putExtra("type", "course")
                }
                "subscription" -> {
                    intent.putExtra("type", "subscription")
                }
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            GlobalData.packagesOptions = PackagesOptions("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ThanksPage, UserDrawer::class.java)
        when (GlobalData.packagesOptions.type) {
            "consultation" -> {
                intent.putExtra("type", "consultation")
            }
            "course" -> {
                intent.putExtra("type", "course")
            }
            "subscription" -> {
                intent.putExtra("type", "subscription")
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        GlobalData.packagesOptions = PackagesOptions("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
    }
}