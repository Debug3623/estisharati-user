package digital.upbeat.estisharati_consultant.UI

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import digital.upbeat.estisharati_consultant.Adapter.SearchUserAdapter
import digital.upbeat.estisharati_consultant.DataClassHelper.DataUser
import digital.upbeat.estisharati_consultant.DataClassHelper.DataUserFireStore
import digital.upbeat.estisharati_consultant.Fragment.Subscribers
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import kotlinx.android.synthetic.main.activity_consultant_drawer.*
import kotlinx.android.synthetic.main.app_bar_consultant_drawer.*
import kotlinx.android.synthetic.main.nav_side_manu.*

class ConsultantDrawer : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var radioEnglish: RadioButton
    lateinit var radioArabic: RadioButton
    lateinit var almarai_bold: Typeface
    lateinit var almarai_regular: Typeface
    lateinit var dataUser: DataUser
    var mBackPressed: Long = 0
    var searchUserdialog: AlertDialog? = null
    lateinit var firestore: FirebaseFirestore
    lateinit var subscribers: Subscribers
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultant_drawer)
        initViews()
        setUserDetails()
        clickEvents()
        navPageClickEvents()
        subscribers = Subscribers()
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, subscribers).commit()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultantDrawer)
        preferencesHelper = SharedPreferencesHelper(this@ConsultantDrawer)
        firestore = FirebaseFirestore.getInstance()

        radioEnglish = language_group.findViewById(R.id.radio_english) as RadioButton
        radioArabic = language_group.findViewById(R.id.radio_arabic) as RadioButton
        almarai_bold = ResourcesCompat.getFont(this@ConsultantDrawer, R.font.almarai_bold)!!
        almarai_regular = ResourcesCompat.getFont(this@ConsultantDrawer, R.font.almarai_regular)!!
        dataUser = preferencesHelper.logInConsultant
        val hashMap = hashMapOf<String, Any>("online_status" to true)
        helperMethods.updateUserDetailsToFirestore(dataUser.id, hashMap)
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
        search_user_icon.setOnClickListener {
            searchUserPopup()
        }
    }

    fun navPageClickEvents() {
        nav_subscribers.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, subscribers).commit()
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
            if (helperMethods.isConnectingToInternet) {
                val intent = Intent(this@ConsultantDrawer, Pages::class.java)
                intent.putExtra("page", "about-us")
                startActivity(intent)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        nav_terms_condition.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                val intent = Intent(this@ConsultantDrawer, Pages::class.java)
                intent.putExtra("page", "terms-and-conditions")
                startActivity(intent)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
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
        jobTitle.text = dataUser.user_metas.job_title
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

    fun searchUserPopup() {
        val LayoutView = LayoutInflater.from(this@ConsultantDrawer).inflate(R.layout.search_user_layout, null)
        val alertDialog = AlertDialog.Builder(this@ConsultantDrawer)
        alertDialog.setView(LayoutView)
        alertDialog.setCancelable(false)
        searchUserdialog = alertDialog.create()
        searchUserdialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val search_text = LayoutView.findViewById<EditText>(R.id.search_text)
        val search_close_btn = LayoutView.findViewById<ImageView>(R.id.search_close_btn)
        val search_not_found = LayoutView.findViewById<LinearLayout>(R.id.search_not_found)
        val search_user_recyclerview = LayoutView.findViewById<RecyclerView>(R.id.search_user_recyclerview)
        search_user_recyclerview.setHasFixedSize(true)
        search_user_recyclerview.removeAllViews()
        search_user_recyclerview.layoutManager = LinearLayoutManager(this@ConsultantDrawer)
        var searchUserAdapter: SearchUserAdapter? = null
        val datauserFirestoreArrayList = arrayListOf<DataUserFireStore>()
        var dataPassed = arrayListOf<DataUserFireStore>()
        val userIds: ArrayList<String> = arrayListOf()
        for (User in subscribers.mySubscriberResponse.data) {
            userIds.add(User.user_id)
        }
        if (userIds.size > 0) {
            firestore.collection("Users").whereIn("user_id", userIds).orderBy("fname", Query.Direction.ASCENDING).get().addOnSuccessListener {
                it?.let {
                    for (data in it) {
                        val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                        if (!dataUserFireStore.user_id.equals(dataUser.id)) {
                            datauserFirestoreArrayList.add(dataUserFireStore)
                        }
                    }
                    searchUserdialog?.show()
                    dataPassed.addAll(datauserFirestoreArrayList)
                    if (dataPassed.size > 0) {
                        search_user_recyclerview.visibility = View.VISIBLE
                        search_not_found.visibility = View.GONE
                        searchUserAdapter = SearchUserAdapter(this@ConsultantDrawer, this@ConsultantDrawer, dataPassed)
                        search_user_recyclerview.adapter = searchUserAdapter
                    } else {
                        search_user_recyclerview.visibility = View.GONE
                        search_not_found.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            search_user_recyclerview.visibility = View.GONE
            search_not_found.visibility = View.VISIBLE
        }
        search_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                dataPassed.clear()
                if (!s.toString().equals("")) {
                    for (data in datauserFirestoreArrayList) {
                        val check = data.fname + " " + data.lname
                        if (check.contains(s.toString(), true)) {
                            dataPassed.add(data)
                        }
                    }
                } else {
                    dataPassed.addAll(datauserFirestoreArrayList)
                }
                if (dataPassed.size > 0) {
                    search_user_recyclerview.visibility = View.VISIBLE
                    search_not_found.visibility = View.GONE
                    searchUserAdapter = SearchUserAdapter(this@ConsultantDrawer, this@ConsultantDrawer, dataPassed)
                    search_user_recyclerview.adapter = searchUserAdapter
                } else {
                    search_user_recyclerview.visibility = View.GONE
                    search_not_found.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        search_close_btn.setOnClickListener { searchUserdialog?.dismiss() }
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

    override fun onDestroy() {
        val hashMap = hashMapOf<String, Any>("online_status" to false, "last_seen" to FieldValue.serverTimestamp())
        helperMethods.updateUserDetailsToFirestore(dataUser.id, hashMap)

        super.onDestroy()
    }

    override fun onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            exitPopup("Exit", "Are you sure?\nDo you want exit the app and offline.")
            return
        } else {
            Toast.makeText(this@ConsultantDrawer, "Please click back again to exit!", Toast.LENGTH_SHORT).show()
        }
        mBackPressed = System.currentTimeMillis()
    }
}
