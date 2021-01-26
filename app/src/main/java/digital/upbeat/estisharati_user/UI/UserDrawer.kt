package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import digital.upbeat.estisharati_user.Utils.alertActionClickListner
import kotlinx.android.synthetic.main.activity_user_drawer.*
import kotlinx.android.synthetic.main.app_bar_user_drawer.*
import kotlinx.android.synthetic.main.nav_side_manu.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class UserDrawer : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var radioEnglish: RadioButton
    lateinit var radioArabic: RadioButton
    lateinit var almarai_bold: Typeface
    lateinit var almarai_regular: Typeface
    lateinit var dataUser: DataUser
    var mBackPressed: Long = 0
    lateinit var retrofitInterface: RetrofitInterface
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
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        radioEnglish = language_group.findViewById(R.id.radio_english) as RadioButton
        radioArabic = language_group.findViewById(R.id.radio_arabic) as RadioButton
        almarai_bold = ResourcesCompat.getFont(this@UserDrawer, R.font.almarai_bold)!!
        almarai_regular = ResourcesCompat.getFont(this@UserDrawer, R.font.almarai_regular)!!
        dataUser = preferencesHelper.logInUser
        val hashMap = hashMapOf<String, Any>("online_status" to true)
        helperMethods.updateUserDetailsToFirestore(dataUser.id, hashMap)
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Home(this@UserDrawer), "Home").commit()
    }

    override fun onStart() {
        super.onStart()
        if (GlobalData.profileUpdate) {
            dataUser = preferencesHelper.logInUser
            GlobalData.profileUpdate = false
            setUserDetails()
        }
    }

    fun clickEvents() {
        if (preferencesHelper.appLang.equals("en")) {
            radioButtonChange(true)
            radioEnglish.isChecked = true
        } else {
            radioButtonChange(false)
            radioArabic.isChecked = true
        }
        language_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_english -> {
                    //                    radioButtonChange(true)
                    preferencesHelper.appLang = "en"
                    GlobalData.BaseUrl = "https://super-servers.com/estisharati/api/v1/${preferencesHelper.appLang}/"
                    Log.d("BaseURL", GlobalData.BaseUrl)
                    GlobalData.homeResponseMain=null
                    startActivity(Intent(this@UserDrawer, SplashScreen::class.java))
                    finish()
                }
                R.id.radio_arabic -> {
                    //                    radioButtonChange(false)
                    preferencesHelper.appLang = "ar"
                    GlobalData.BaseUrl = "https://super-servers.com/estisharati/api/v1/${preferencesHelper.appLang}/"
                    Log.d("BaseURL", GlobalData.BaseUrl)
                    GlobalData.homeResponseMain=null
                    startActivity(Intent(this@UserDrawer, SplashScreen::class.java))
                    finish()
                }
            }
        }

        menu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START, true)
        }
        notificationLayout.setOnClickListener {
            startActivity(Intent(this@UserDrawer, Notifications::class.java))
        }
        searchLayout.setOnClickListener {
            startActivity(Intent(this@UserDrawer, Search::class.java))
        }
    }

    fun navConsultations() {
        action_bar_logo.visibility = View.GONE
        action_bar_title.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Consultations(this@UserDrawer), "Consultations").commit()
    }

    fun navHome() {
        action_bar_logo.visibility = View.VISIBLE
        action_bar_title.visibility = View.GONE
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Home(this@UserDrawer), "Home").commit()
    }

    fun navPageClickEvents() {
        nav_home.setOnClickListener {
            action_bar_logo.visibility = View.VISIBLE
            action_bar_title.visibility = View.GONE
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Home(this@UserDrawer), "Home").commit()
            drawer_layout.closeDrawer(GravityCompat.START, true)
        }
        nav_offers.setOnClickListener {
            startActivity(Intent(this@UserDrawer, Offers::class.java))
        }
        nav_consultations.setOnClickListener {
            action_bar_logo.visibility = View.GONE
            action_bar_title.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Consultations(this@UserDrawer), "Consultations").commit()
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
        nav_my_packages.setOnClickListener {
            startActivity(Intent(this@UserDrawer, MyPackages::class.java))
        }
        nav_blog.setOnClickListener {
            startActivity(Intent(this@UserDrawer, Blog::class.java))
        }
        nav_survey.setOnClickListener {
            startActivity(Intent(this@UserDrawer, Survey::class.java))
        }
        nav_favorites.setOnClickListener {
            startActivity(Intent(this@UserDrawer, Favorites::class.java))
        }
        nav_appointment.setOnClickListener {
            startActivity(Intent(this@UserDrawer, MyAppointment::class.java))
        }
        nav_invite_app.setOnClickListener {
            startActivity(Intent(this@UserDrawer, InviteApp::class.java))
        }
        nav_about_app.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                val intent = Intent(this@UserDrawer, Pages::class.java)
                intent.putExtra("page", "about-us")
                startActivity(intent)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        nav_terms_condition.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                val intent = Intent(this@UserDrawer, Pages::class.java)
                intent.putExtra("page", "terms-and-conditions")
                startActivity(intent)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        nav_refund.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                val intent = Intent(this@UserDrawer, Pages::class.java)
                intent.putExtra("page", "refund-policy")
                startActivity(intent)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        nav_chat_home.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                startActivity(Intent(this@UserDrawer, ChatHome::class.java))
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }

        nav_faq.setOnClickListener {
            startActivity(Intent(this@UserDrawer, FAQ::class.java))
        }
        nav_contect_us.setOnClickListener {
            startActivity(Intent(this@UserDrawer, ContactUs::class.java))
        }
        nav_header1.setOnClickListener {
            startActivity(Intent(this@UserDrawer, MyProfile::class.java))
        }
        nav_header2.setOnClickListener {
            startActivity(Intent(this@UserDrawer, MyProfile::class.java))
        }
        nav_logout.setOnClickListener {
            LogOutPopup(getString(R.string.logout), getString(R.string.are_you_sure_do_you_want_to_logout))
        }
        upgradePackage.setOnClickListener {
            val intent = Intent(this@UserDrawer, Packages::class.java)
            intent.putExtra("viaFrom", "Home")
            startActivity(intent)
        }
    }

    fun setUserDetails() {
        user_name.text = "${dataUser.fname} ${dataUser.lname}"
        Glide.with(this@UserDrawer).load(dataUser.image).apply(helperMethods.profileRequestOption).into(user_image)
        package_name.text = dataUser.subscription.package_count + "  " + getString(R.string.package_)
    }

    fun LogOutPopup(titleStr: String, messageStr: String) {
        helperMethods.showAlertDialog(this@UserDrawer, object : alertActionClickListner {
            override fun onActionOk() {
                if (helperMethods.isConnectingToInternet) {
                    logoutApiCall()
                } else {
                    helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                }
            }

            override fun onActionCancel() {
            }
        }, titleStr, messageStr, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))
    }

    fun logoutApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.LOGOUT_API_CALL("Bearer ${dataUser.access_token}", "")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
                            } else {
                                val message = jsonObject.getString("message")
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), message)
                            }
                        } catch (e: JSONException) {
                            helperMethods.showToastMessage(getString(R.string.something_went_wrong_on_backend_server))
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("body", "Body Empty")
                    }
                } else {
                    helperMethods.showToastMessage(getString(R.string.something_went_wrong))
                    Log.d("body", "Not Successful")
                }
                makeLogOutAndClearData()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
                makeLogOutAndClearData()
            }
        })
    }

    fun makeLogOutAndClearData() {
        preferencesHelper.isUserLogIn = false
        preferencesHelper.logInUser = DataUser()
        val intent = Intent(this@UserDrawer, LoginAndRegistration::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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

    fun exitPopup(titleStr: String, messageStr: String) {
        helperMethods.showAlertDialog(this@UserDrawer, object : alertActionClickListner {
            override fun onActionOk() {
                finish()
            }

            override fun onActionCancel() {
            }
        }, titleStr, messageStr, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))
    }

    override fun onDestroy() {
        val hashMap = hashMapOf<String, Any>("online_status" to false, "last_seen" to FieldValue.serverTimestamp())
        helperMethods.updateUserDetailsToFirestore(dataUser.id, hashMap)

        super.onDestroy()
    }

    override fun onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            exitPopup(getString(R.string.exit), getString(R.string.are_you_sure_do_you_want_exit_the_app_and_offline))
            return
        } else {
            Toast.makeText(this@UserDrawer, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()
        }
        mBackPressed = System.currentTimeMillis()
    }
}
