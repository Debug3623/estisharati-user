package estisharatibussiness.users.com.UserInterface

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import estisharatibussiness.users.com.Adapter.OffersConsultantsAdapter
import estisharatibussiness.users.com.Adapter.OffersCoursesAdapter
import estisharatibussiness.users.com.Adapter.PackageAdapter
import estisharatibussiness.users.com.ApiHelper.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelper.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelper.Login.DataUser
import estisharatibussiness.users.com.DataClassHelper.Offers.OffersResponse
import estisharatibussiness.users.com.DataClassHelper.Offers.Package
import estisharatibussiness.users.com.DataClassHelper.PackagesOptions.PackagesOptions
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_offers.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ActivityOffers : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    var currentTab = "consultant"
    var offersresponse: OffersResponse = OffersResponse(arrayListOf(), arrayListOf(), arrayListOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        initViews()
        clickEvents()
    }

    private fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        offersConsultants.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.white))
        offersCourses.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.transparent_white))
        offersPackages.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.transparent_white))
        offersConsultants.setOnClickListener {
            currentTab = "consultant"
            initializeOffersRecyclerview()
        }
        offersCourses.setOnClickListener {
            currentTab = "courses"
            initializeOffersRecyclerview()
        }
        offersPackages.setOnClickListener {
            currentTab = "packages"
            initializeOffersRecyclerview()
        }
        notificationLayout.setOnClickListener {
            startActivity(Intent(this@ActivityOffers, Notifications::class.java))
        }
    }

    private fun initViews() {
        helperMethods = HelperMethods(this@ActivityOffers)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ActivityOffers)
        dataUser = sharedPreferencesHelper.logInUser
        if (GlobalData.isThingInitialized()) {
            notificationCount.text = GlobalData.homeResponse.notification_count
        }
        if (helperMethods.isConnectingToInternet) {
            OffersListApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun setTabConsultant() {
        if (currentTab.equals("consultant")) {
            if (offersresponse.consultants.size > 0) {
                offersRecycler.visibility = View.VISIBLE
                offersEmptyLayout.visibility = View.GONE
            } else {
                offersRecycler.visibility = View.GONE
                offersEmptyLayout.visibility = View.VISIBLE
                offer_errorText.text = getString(R.string.there_is_no_offer_consultant_available)
            }
            offersConsultants.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.white))
            offersCourses.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.transparent_white))
            offersPackages.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.transparent_white))
        }
    }

    fun setTabCourse() {
        if (currentTab.equals("courses")) {
            if (offersresponse.courses.size > 0) {
                offersRecycler.visibility = View.VISIBLE
                offersEmptyLayout.visibility = View.GONE
            } else {
                offersRecycler.visibility = View.GONE
                offersEmptyLayout.visibility = View.VISIBLE
                offer_errorText.text = getString(R.string.there_is_no_offer_courses_available)
            }
            offersCourses.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.white))
            offersConsultants.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.transparent_white))
            offersPackages.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.transparent_white))
        }
    }

    fun setTabPackages() {
        if (currentTab.equals("packages")) {
            if (offersresponse.packages.size > 0) {
                offersRecycler.visibility = View.VISIBLE
                offersEmptyLayout.visibility = View.GONE
            } else {
                offersRecycler.visibility = View.GONE
                offersEmptyLayout.visibility = View.VISIBLE
                offer_errorText.text = getString(R.string.there_is_no_packages_available)
            }
            offersPackages.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.white))
            offersCourses.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.transparent_white))
            offersConsultants.setTextColor(ContextCompat.getColor(this@ActivityOffers, R.color.transparent_white))
        }
    }

    fun initializeOffersRecyclerview() {
        offersRecycler.setHasFixedSize(true)
        offersRecycler.removeAllViews()
        offersRecycler.layoutManager = LinearLayoutManager(this@ActivityOffers)
        if (currentTab.equals("consultant")) {
            offersRecycler.adapter = OffersConsultantsAdapter(this@ActivityOffers, this@ActivityOffers, offersresponse.consultants)
            setTabConsultant()
        } else if (currentTab.equals("courses")) {
            offersRecycler.adapter = OffersCoursesAdapter(this@ActivityOffers, this@ActivityOffers, offersresponse.courses)
            setTabCourse()
        } else if (currentTab.equals("packages")) {
            offersRecycler.adapter = PackageAdapter(this@ActivityOffers, null, null, this@ActivityOffers, arrayListOf(), offersresponse.packages)
            setTabPackages()
        }
    }

    fun OffersListApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.OFFERS_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val data = jsonObject.getString("data")
                                offersresponse = Gson().fromJson(data, OffersResponse::class.java)
//                                Log.d("response", offersresponse.toString())
                                initializeOffersRecyclerview()
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
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun choosePackage(packages: Package) {
        GlobalData.packagesOptions = PackagesOptions(packages.subscription.id, packages.subscription.name, "subscription", "", "0", "0", "0", packages.offerprice, "0", "0", "", "", "0", "0", "", "0", "0")
        startActivity(Intent(this@ActivityOffers, ActivityPackagesSelection::class.java))
    }
}