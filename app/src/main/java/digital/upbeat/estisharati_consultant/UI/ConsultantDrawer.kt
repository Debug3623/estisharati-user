package digital.upbeat.estisharati_consultant.UI

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_consultant.Fragment.Subscribers
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import kotlinx.android.synthetic.main.activity_consultant_drawer.*
import kotlinx.android.synthetic.main.app_bar_consultant_drawer.*
import kotlinx.android.synthetic.main.nav_side_manu.*
import kotlinx.android.synthetic.main.nav_side_manu.view.*

class ConsultantDrawer : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var radioEnglish: RadioButton
    lateinit var radioArabic: RadioButton
    lateinit var almarai_bold: Typeface
    lateinit var almarai_regular: Typeface
    lateinit var dataUser: DataUser
    var mBackPressed: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultant_drawer)
        initViews()
        setUserDetails()
        clickEvents()
        navPageClickEvents()
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Subscribers()).commit()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultantDrawer)
        preferencesHelper = SharedPreferencesHelper(this@ConsultantDrawer)
        radioEnglish = language_group.findViewById(R.id.radio_english) as RadioButton
        radioArabic = language_group.findViewById(R.id.radio_arabic) as RadioButton
        almarai_bold = ResourcesCompat.getFont(this@ConsultantDrawer, R.font.almarai_bold)!!
        almarai_regular = ResourcesCompat.getFont(this@ConsultantDrawer, R.font.almarai_regular)!!
        dataUser = preferencesHelper.logInConsultant
    }

    override fun onStart() {
        super.onStart()
        if (GlobalData.profileUpdate) {
            dataUser = preferencesHelper.logInConsultant
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
        notification_layout.setOnClickListener {}
    }

    fun navPageClickEvents() {
        nav_subscribers.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Subscribers()).commit()
            drawer_layout.closeDrawer(GravityCompat.START, true)
        }
        //        nav_consultations.setOnClickListener {
        //            action_bar_logo.visibility = View.GONE
        //            action_bar_title.visibility = View.VISIBLE
        //            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Consultations()).commit()
        //            drawer_layout.closeDrawer(GravityCompat.START, true)
        //        }
        notification_layout.setOnClickListener {
            startActivity(Intent(this@ConsultantDrawer, Notifications::class.java))
        }
        nav_my_profile.setOnClickListener {
            startActivity(Intent(this@ConsultantDrawer, MyProfile::class.java))
        }
        nav_about_app.setOnClickListener {
            startActivity(Intent(this@ConsultantDrawer, AboutUs::class.java))
        }

        nav_header.setOnClickListener {
            startActivity(Intent(this@ConsultantDrawer, MyProfile::class.java))
        }

        nav_help.setOnClickListener {
            startActivity(Intent(this@ConsultantDrawer, Help::class.java))
        }
        nav_logout.setOnClickListener {
            LogOutPopup("LogOut", "Are you sure?\n" + "Do you want to logout !")
        }
    }

    fun setUserDetails() {
        user_name.text = "${dataUser.fname} ${dataUser.lname}"
        Glide.with(this@ConsultantDrawer).load(dataUser.image).apply(helperMethods.profileRequestOption).into(user_image)
    }

    fun LogOutPopup(titleStr: String, messageStr: String) {
        val LayoutView = LayoutInflater.from(this@ConsultantDrawer).inflate(R.layout.confirmation_alert_popup, null)
        val aleatdialog = AlertDialog.Builder(this@ConsultantDrawer)
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
            preferencesHelper.isConsultantLogIn = false
            preferencesHelper.logInConsultant = DataUser()
            val intent = Intent(this@ConsultantDrawer, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        action_cancel.setOnClickListener { dialog.dismiss() }
    }

    fun radioButtonChange(ifEnglis: Boolean) {
        if (ifEnglis) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioEnglish.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ConsultantDrawer, R.color.orange))
                radioArabic.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ConsultantDrawer, R.color.black))
            }
            radioEnglish.setTextColor(ContextCompat.getColor(this@ConsultantDrawer, R.color.orange));
            radioArabic.setTextColor(ContextCompat.getColor(this@ConsultantDrawer, R.color.black));

            radioEnglish.typeface = almarai_bold
            radioArabic.typeface = almarai_regular
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioEnglish.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ConsultantDrawer, R.color.black))
                radioArabic.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ConsultantDrawer, R.color.orange))
            }
            radioEnglish.setTextColor(ContextCompat.getColor(this@ConsultantDrawer, R.color.black));
            radioArabic.setTextColor(ContextCompat.getColor(this@ConsultantDrawer, R.color.orange));

            radioEnglish.typeface = almarai_regular
            radioArabic.typeface = almarai_bold
        }
    }

    fun exitPopup(titleStr: String, messageStr: String) {
        val LayoutView = LayoutInflater.from(this@ConsultantDrawer).inflate(R.layout.confirmation_alert_popup, null)
        val aleatdialog = AlertDialog.Builder(this@ConsultantDrawer)
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
            finish()
        }
        action_cancel.setOnClickListener { dialog.dismiss() }
    }

    override fun onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            exitPopup("Exit", "Are you sure?\nDo you want exit and offline tha app.")
            return
        } else {
            helperMethods.showToastMessage("Please click back again to exit!")
        }
        mBackPressed = System.currentTimeMillis()
    }
}
