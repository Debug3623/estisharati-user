package estisharatibussiness.users.com.UserInterfaces

import android.content.Intent
import android.os.Bundle
import estisharatibussiness.users.com.DataClassHelperMehtods.PackagesOptions.PackagesOptions
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_thanks_page.*

class ThanksPageActivity : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thanks_page)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ThanksPageActivity)
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
            val intent = Intent(this@ThanksPageActivity, UserDrawerActivity::class.java)
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
        val intent = Intent(this@ThanksPageActivity, UserDrawerActivity::class.java)
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