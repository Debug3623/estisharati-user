package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_user_drawer.*
import kotlinx.android.synthetic.main.app_bar_user_drawer.*
import kotlinx.android.synthetic.main.nav_side_manu.*

class UserDrawer : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var radioEnglish: RadioButton
    lateinit var radioArabic: RadioButton
    lateinit var almarai_bold: Typeface
    lateinit var almarai_regular: Typeface
    lateinit var dataUser: DataUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_drawer)
        initViews()
        setUserDetails()
        clickEvents()
        navPageClickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@UserDrawer)
        preferencesHelper = SharedPreferencesHelper(this@UserDrawer)
        radioEnglish = language_group.findViewById(R.id.radio_english) as RadioButton
        radioArabic = language_group.findViewById(R.id.radio_arabic) as RadioButton
        almarai_bold = ResourcesCompat.getFont(this@UserDrawer, R.font.almarai_bold)!!
        almarai_regular = ResourcesCompat.getFont(this@UserDrawer, R.font.almarai_regular)!!
        dataUser = preferencesHelper.getLogInUser()


        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Home()).commit()
    }

    override fun onStart() {
        super.onStart()
        if (GlobalData.profileUpdate) {
            dataUser = preferencesHelper.getLogInUser()
            GlobalData.profileUpdate = false
            setUserDetails()
        }
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
        notification_layout.setOnClickListener {
            startActivity(Intent(this@UserDrawer, Notifications::class.java))
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
        nav_my_consultations.setOnClickListener {
            startActivity(Intent(this@UserDrawer, MyConsultations::class.java))
        }
        nav_online_courses.setOnClickListener {
            startActivity(Intent(this@UserDrawer, OnlineCourses::class.java))
        }
        nav_my_courses.setOnClickListener {
            startActivity(Intent(this@UserDrawer, MyCourses::class.java))
        }
        nav_about_app.setOnClickListener {
            startActivity(Intent(this@UserDrawer, AboutUs::class.java))
        }
        nav_contect_us.setOnClickListener {
            startActivity(Intent(this@UserDrawer, ContactUs::class.java))
        }
        nav_my_profile1.setOnClickListener {
            startActivity(Intent(this@UserDrawer, MyProfile::class.java))
        }
        nav_my_profile2.setOnClickListener {
            startActivity(Intent(this@UserDrawer, MyProfile::class.java))
        }
        nav_logout.setOnClickListener {
            LogOutPopup("LogOut", "Are you sure?\n" + "Do you want to logout !")
        }
    }

    fun setUserDetails() {
        user_name.text = "${dataUser.fname} ${dataUser.lname}"
        Glide.with(this@UserDrawer).load(dataUser.image).apply(helperMethods.profileRequestOption).into(user_image)
        package_name.text = "${dataUser.subscription.current_package} Package"
    }

    fun LogOutPopup(titleStr: String, messageStr: String) {
        val LayoutView = LayoutInflater.from(this@UserDrawer).inflate(R.layout.confirmation_alert_popup, null)
        val aleatdialog = AlertDialog.Builder(this@UserDrawer)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val title = LayoutView.findViewById<TextView>(R.id.title)
        val message = LayoutView.findViewById<TextView>(R.id.message)
        val action_ok = LayoutView.findViewById<TextView>(R.id.action_ok)
        val action_cancel = LayoutView.findViewById<TextView>(R.id.action_cancel)
        title.text = titleStr
        message.text = messageStr
        action_ok.setOnClickListener {
            dialog.dismiss()
            preferencesHelper.isUserLogIn = false
            preferencesHelper.setLogInUser(DataUser())
            val intent = Intent(this@UserDrawer, LoginAndRegistration::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        action_cancel.setOnClickListener { dialog.dismiss() }
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
