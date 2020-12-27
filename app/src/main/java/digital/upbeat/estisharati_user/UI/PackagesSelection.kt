package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.PaymentType.PTResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_course_details.*
import kotlinx.android.synthetic.main.activity_packages_selection.*
import kotlinx.android.synthetic.main.activity_packages_selection.nav_back
import kotlinx.android.synthetic.main.add_coupon_layout.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PackagesSelection : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packages_selection)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@PackagesSelection)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@PackagesSelection)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        change_packages.setOnClickListener {   val intent = Intent(this@PackagesSelection, Packages::class.java)
            intent.putExtra("viaFrom", "packagesSelection")
            startActivity(intent) }
        proceed.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                subscriptionApiCall()
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        addCouponLayout.setOnClickListener {
            showCouponAddPopup()
        }
    }

    override fun onStart() {
        super.onStart()
        setChoosenDetails()
    }

    fun setChoosenDetails() {
        chooseName.text = GlobalData.packagesOptions.name
        choosePrice.text = GlobalData.packagesOptions.price
        chooseDiscount.text = "- " + resources.getString(R.string.aed) + " " + GlobalData.packagesOptions.discount
        if (GlobalData.packagesOptions.discount.equals("")) {
            chooseDiscount.visibility = View.GONE
        } else {
            chooseDiscount.visibility = View.VISIBLE
        }
        chooseType.text = when (GlobalData.packagesOptions.type) {
            "course" -> {
                "Course"
            }
            "consultation" -> {
                "Consultation"
            }
            "subscription" -> {
                "Packages"
            }
            else -> ""
        }
    }

    fun showCouponAddPopup() {
        val LayoutView = LayoutInflater.from(this@PackagesSelection).inflate(R.layout.add_coupon_layout, null)
        val aleatdialog = AlertDialog.Builder(this@PackagesSelection)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        LayoutView.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        LayoutView.enterBtn.setOnClickListener {
            if (LayoutView.couponCode.text.toString().equals("")) {
                helperMethods.showToastMessage("")
                return@setOnClickListener
            }
            if (!helperMethods.isConnectingToInternet) {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                return@setOnClickListener
            }
            dialog.dismiss()
            couponTypeApiCall(GlobalData.packagesOptions.id, GlobalData.packagesOptions.type, LayoutView.couponCode.text.toString())
        }
    }

    fun couponTypeApiCall(id: String, type: String, code: String) {
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.COUPON_API_CALL("Bearer ${dataUser.access_token}", id, type, code)
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
                                val dataObject = JSONObject(data)
                                GlobalData.packagesOptions.coupon_id = dataObject.getString("coupon_id")
                                GlobalData.packagesOptions.coupon_code = code
                                GlobalData.packagesOptions.price = dataObject.getString("offerprice")
                                GlobalData.packagesOptions.discount = dataObject.getString("discount")
                                helperMethods.showToastMessage("Coupon added successfully !")
                                setChoosenDetails()
                            } else {
                                val message = jsonObject.getString("message")

                                helperMethods.AlertPopup("Alert", message)
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
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun subscriptionApiCall() {
        helperMethods.showProgressDialog("Please wait while loading...")
        var subscription_id = ""
        var course_id = ""
        var consultant_id = ""
        when (GlobalData.packagesOptions.type) {
            "course" -> {
                course_id = GlobalData.packagesOptions.id
            }
            "consultation" -> {
                consultant_id = GlobalData.packagesOptions.id
            }
            "subscription" -> {
                subscription_id = GlobalData.packagesOptions.id
            }
            else -> {
            }
        }
        val responseBodyCall = retrofitInterface.USER_SUBSCRIPTION_API_CALL("Bearer ${dataUser.access_token}", GlobalData.packagesOptions.type, subscription_id, course_id, consultant_id, GlobalData.packagesOptions.price, "1", "1234_123123_1234", GlobalData.packagesOptions.coupon_id, GlobalData.packagesOptions.coupon_code, GlobalData.packagesOptions.discount)

        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            val message = jsonObject.getString("message")
                            if (status.equals("200")) {
                                helperMethods.showToastMessage(message)
                                startActivity(Intent(this@PackagesSelection, ThanksPage::class.java))
                            } else {
                                helperMethods.AlertPopup("Alert", message)
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
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }
}