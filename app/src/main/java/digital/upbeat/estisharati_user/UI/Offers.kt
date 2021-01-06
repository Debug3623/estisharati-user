package digital.upbeat.estisharati_user.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.OffersConsultantsAdapter
import digital.upbeat.estisharati_user.Adapter.OffersCoursesAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Offers.OffersResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_offers.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Offers : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    var currentTab = "consultant"
    var offersresponse: OffersResponse = OffersResponse(arrayListOf(), arrayListOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        initViews()
        clickEvents()
    }

    private fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        offersConsultants.setTextColor(ContextCompat.getColor(this@Offers, R.color.white))
        offersCourses.setTextColor(ContextCompat.getColor(this@Offers, R.color.transparent_white))

        offersConsultants.setOnClickListener {
            currentTab = "consultant"
            initializeOffersConsultantRecyclerview()
        }

        offersCourses.setOnClickListener {
            currentTab = "courses"
            initializeOffersConsultantRecyclerview()
        }
        notificationLayout.setOnClickListener {
            startActivity(Intent(this@Offers, Notifications::class.java))
        }
    }

    private fun initViews() {
        helperMethods = HelperMethods(this@Offers)
        helperMethods = HelperMethods(this@Offers)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@Offers)
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
            offersConsultants.setTextColor(ContextCompat.getColor(this@Offers, R.color.white))
            offersCourses.setTextColor(ContextCompat.getColor(this@Offers, R.color.transparent_white))
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
            offersCourses.setTextColor(ContextCompat.getColor(this@Offers, R.color.white))
            offersConsultants.setTextColor(ContextCompat.getColor(this@Offers, R.color.transparent_white))
        }
    }

    fun initializeOffersConsultantRecyclerview() {
        offersRecycler.setHasFixedSize(true)
        offersRecycler.removeAllViews()
        offersRecycler.layoutManager = LinearLayoutManager(this@Offers)
        if (currentTab.equals("consultant")) {
            offersRecycler.adapter = OffersConsultantsAdapter(this@Offers, this@Offers, offersresponse.consultants)
            setTabConsultant()
        } else if (currentTab.equals("courses")) {
            offersRecycler.adapter = OffersCoursesAdapter(this@Offers, this@Offers, offersresponse.courses)
            setTabCourse()
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
                                initializeOffersConsultantRecyclerview()
                            } else {
                                val message = jsonObject.getString("message")
                                helperMethods.AlertPopup(getString(R.string.alert), message)
                                initializeOffersConsultantRecyclerview()
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
}