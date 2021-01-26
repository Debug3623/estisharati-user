package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Referral.ReferralResponse
import digital.upbeat.estisharati_user.DataClassHelper.Subscription.SubscriptionResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
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
import java.util.*

class PackagesSelection : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser
    lateinit var referralResponse: ReferralResponse
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
        change_packages.setOnClickListener {
            val intent = Intent(this@PackagesSelection, Packages::class.java)
            intent.putExtra("viaFrom", "packagesSelection")
            startActivity(intent)
        }
        proceed.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                Log.d("Subscription", Gson().toJson(GlobalData.packagesOptions))
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
        if (helperMethods.isConnectingToInternet) {
            referralApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun setChoosenDetails() {
        var TransactionAmount = 0.0
        var totelDiscountAmount = 0.0

        TransactionAmount = GlobalData.packagesOptions.originalPrice.toDouble() - GlobalData.packagesOptions.discount.toDouble()
        val referralAmount =  (TransactionAmount / 100.0f) * referralResponse.data.points.toDouble()
        TransactionAmount -= referralAmount
        totelDiscountAmount += GlobalData.packagesOptions.discount.toDouble()
        totelDiscountAmount += referralAmount
        val vatAmount = TransactionAmount * 0.05
        TransactionAmount += vatAmount

        GlobalData.packagesOptions.transaction_amount = helperMethods.convetDecimalFormat(TransactionAmount)
        GlobalData.packagesOptions.vat_amount = helperMethods.convetDecimalFormat(vatAmount)

        if (referralResponse.data.points.toDouble() > 0) {
            GlobalData.packagesOptions.referral = "true"
            GlobalData.packagesOptions.referral_code = referralResponse.data.referral_code
            GlobalData.packagesOptions.referral_percent = referralResponse.data.points
            GlobalData.packagesOptions.referral_discount = helperMethods.convetDecimalFormat(referralAmount)
            referralAmountTxt.text = "(" + GlobalData.packagesOptions.referral_percent + " %)  " + GlobalData.packagesOptions.referral_discount + " " + getString(R.string.aed)
        } else {
            GlobalData.packagesOptions.referral = "false"
            GlobalData.packagesOptions.referral_code = ""
            GlobalData.packagesOptions.referral_percent = "0"
            GlobalData.packagesOptions.referral_discount = "0"
        }


        chooseName.text = GlobalData.packagesOptions.name
        choosePrice.text = GlobalData.packagesOptions.transaction_amount
        originalPrice.text = resources.getString(R.string.aed) + " " + GlobalData.packagesOptions.originalPrice



        chooseDiscount.text = "- " + resources.getString(R.string.aed) + " " + helperMethods.convetDecimalFormat(totelDiscountAmount)
        vatAmountTxt.text = "${resources.getString(R.string.aed)} ${GlobalData.packagesOptions.vat_amount} ${resources.getString(R.string.vat_5)}"
        if (totelDiscountAmount > 0) {
            chooseDiscount.visibility = View.VISIBLE
        } else {
            chooseDiscount.visibility = View.GONE
        }
        chooseType.text = when (GlobalData.packagesOptions.type) {
            "course" -> {
                "Course"
            }
            "consultation" -> {
                "Consultation"
            }
            "subscription" -> {
                "Package"
            }
            else -> ""
        }

        if (!referralResponse.data.points.equals("0")) {
            referralDiscountLayout.visibility = View.VISIBLE
        } else {
            referralDiscountLayout.visibility = View.GONE
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
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
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
                                GlobalData.packagesOptions.discount = dataObject.getString("discount")
                                helperMethods.showToastMessage(getString(R.string.coupon_added_successfully))
                                setChoosenDetails()
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

    fun referralApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.REFERRAL_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            referralResponse = Gson().fromJson(response.body()!!.string(), ReferralResponse::class.java)
                            if (referralResponse.status.equals("200")) {
                                setChoosenDetails()
                            } else {
                                if (helperMethods.checkTokenValidation(referralResponse.status, referralResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.showToastMessage(referralResponse.message)

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

    fun subscriptionApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
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
        val responseBodyCall = retrofitInterface.USER_SUBSCRIPTION_API_CALL("Bearer ${dataUser.access_token}", GlobalData.packagesOptions.type, GlobalData.packagesOptions.category_id, subscription_id, course_id, consultant_id, GlobalData.packagesOptions.transaction_amount, GlobalData.packagesOptions.vat_amount, "1", UUID.randomUUID().toString(), GlobalData.packagesOptions.coupon_id, GlobalData.packagesOptions.coupon_code, GlobalData.packagesOptions.discount, GlobalData.packagesOptions.referral_code, GlobalData.packagesOptions.referral_discount, GlobalData.packagesOptions.referral_percent)

        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val subscriptionResponse: SubscriptionResponse = Gson().fromJson(response.body()!!.string(), SubscriptionResponse::class.java)

                            if (subscriptionResponse.status.equals("200")) {
                                helperMethods.showToastMessage(subscriptionResponse.message)
                                dataUser.subscription.consultations = subscriptionResponse.data.consultations
                                dataUser.subscription.courses = subscriptionResponse.data.courses
                                dataUser.subscription.package_count = subscriptionResponse.data.package_count
                                sharedPreferencesHelper.logInUser = dataUser
                                startActivity(Intent(this@PackagesSelection, ThanksPage::class.java))
                            } else {
                                if (helperMethods.checkTokenValidation(subscriptionResponse.status, subscriptionResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), subscriptionResponse.message)
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