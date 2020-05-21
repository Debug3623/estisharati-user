package digital.upbeat.estisharati_user.UI

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_user_drawer.*
import kotlinx.android.synthetic.main.app_bar_user_drawer.*
import kotlinx.android.synthetic.main.nav_side_manu.*

class UserDrawer : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var radioEnglish: RadioButton
    lateinit var radioArabic: RadioButton
    lateinit var almarai_bold: Typeface
    lateinit var almarai_regular: Typeface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_drawer)
        initViews()
        clickEvents()
        navPageClickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@UserDrawer)
        radioEnglish = language_group.findViewById(R.id.radio_english) as RadioButton
        radioArabic = language_group.findViewById(R.id.radio_arabic) as RadioButton
        almarai_bold = ResourcesCompat.getFont(this@UserDrawer, R.font.almarai_bold)!!
        almarai_regular = ResourcesCompat.getFont(this@UserDrawer, R.font.almarai_regular)!!


        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Consultations()).commit()
    }

    fun clickEvents() {
        radioButtonChange(true)
        language_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_english -> {
                    radioButtonChange(true)
                }
                R.id.radio_arabic -> {
                    radioButtonChange(false)
                }
            }
        }

        menu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START, true)
        }
    }

    fun navPageClickEvents() {
        nav_home.setOnClickListener {
            action_bar_logo.visibility = View.VISIBLE
            action_bar_title.visibility = View.GONE
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Home()).commit()
            drawer_layout.closeDrawer(GravityCompat.START, true)
        }
        nav_consultations.setOnClickListener {
            action_bar_logo.visibility = View.GONE
            action_bar_title.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Consultations()).commit()
            drawer_layout.closeDrawer(GravityCompat.START, true)
        }
    }

    fun radioButtonChange(ifEnglis: Boolean) {
        if (ifEnglis) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioEnglish.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@UserDrawer, R.color.orange))
                radioArabic.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@UserDrawer, R.color.black))
            }
            radioEnglish.setTextColor(ContextCompat.getColor(this@UserDrawer, R.color.orange));
            radioArabic.setTextColor(ContextCompat.getColor(this@UserDrawer, R.color.black));

            radioEnglish.typeface = almarai_bold
            radioArabic.typeface = almarai_regular
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioEnglish.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@UserDrawer, R.color.black))
                radioArabic.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@UserDrawer, R.color.orange))
            }
            radioEnglish.setTextColor(ContextCompat.getColor(this@UserDrawer, R.color.black));
            radioArabic.setTextColor(ContextCompat.getColor(this@UserDrawer, R.color.orange));

            radioEnglish.typeface = almarai_regular
            radioArabic.typeface = almarai_bold
        }
    }
}
