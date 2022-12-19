package estisharatibussiness.users.com.UserInterfaces

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.Referral.ReferralResponse
import estisharatibussiness.users.com.DataClassHelperMehtods.Subscription.SubscriptionResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import estisharatibussiness.users.com.UtilsClasses.alertActionClickListner
import estisharatibussiness.users.com.networkPayment.NetworkModel.NetworkResponse
import estisharatibussiness.users.com.networkPayment.NetwrokPayment
import kotlinx.android.synthetic.main.activity_packages_selection.*
import kotlinx.android.synthetic.main.activity_packages_selection.nav_back
import kotlinx.android.synthetic.main.add_coupon_layout.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import payment.sdk.android.PaymentClient
import payment.sdk.android.cardpayment.CardPaymentData
import payment.sdk.android.cardpayment.CardPaymentRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class ActivityPackagesSelection : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser
    lateinit var referralResponse: ReferralResponse
    lateinit var paymentNetworkResponse: NetworkResponse
//    lateinit var consultantDetailsResponse: ConsultantDetailsResponse
    val PaymentResponseCode = 1996
    var appointmentDate = "0"
    var appointmentTime = "0"
    var consultantId = ""
    var categoryId = ""
    var condition = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packages_selection)

        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityPackagesSelection)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ActivityPackagesSelection)
        dataUser = sharedPreferencesHelper.logInUser

        appointmentDate= intent.getStringExtra("appointment_date").toString()
        appointmentTime= intent.getStringExtra("appointment_time").toString()
        consultantId= intent.getStringExtra("consultant_id").toString()
        categoryId= intent.getStringExtra("category_id").toString()
        condition= intent.getStringExtra("condition").toString()

        Log.d("appointmentDate",appointmentDate)
        Log.d("appointmentTime",appointmentTime)
        Log.d("consultant_id",consultantId)
        Log.d("category_id",categoryId)
        Log.d("condition",condition)
        Log.d("BearerToken ","${dataUser.access_token}")
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        change_packages.setOnClickListener {
            val intent = Intent(this@ActivityPackagesSelection, ActivityPackages::class.java)
            intent.putExtra("viaFrom", "packagesSelection")
            startActivity(intent)
        }
        proceed.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))

                if (referralResponse.android_pay == "2") {
                    subscriptionApiCall()
                } else {
                    NetwrokPayment().getToken(this@ActivityPackagesSelection)
                }
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        addCouponLayout.setOnClickListener {
            showCouponAddPopup()
        }
    }

    fun tokenError(error: String) {
        helperMethods.dismissProgressDialog()
        helperMethods.showToastMessage(error)
    }

    fun callOrderFroPaymentAPI(token: String) {
        NetwrokPayment().createOrder(this, token, GlobalData.packagesOptions.transaction_amount, sharedPreferencesHelper.appLang)
    }

    fun lunchPaymentGateway(Response: String) {
        helperMethods.dismissProgressDialog()

        try {
            paymentNetworkResponse = Gson().fromJson(Response, NetworkResponse::class.java)
            Log.d("getOrderReference", paymentNetworkResponse._embedded.payment.get(0).orderReference)

            Log.d("herew", paymentNetworkResponse._links.payment.href)
            val builder: CardPaymentRequest.Builder = CardPaymentRequest.builder()
            Log.d("Auth", paymentNetworkResponse._links.paymentAuthorization.href)
            builder.gatewayUrl(paymentNetworkResponse._links.paymentAuthorization.href)
            val codeSpliter = paymentNetworkResponse._links.payment.href.split("=")
            Log.d("AuthCode", codeSpliter[1])
            builder.code(codeSpliter[1])
            val req: CardPaymentRequest = builder.build()
            val p = PaymentClient(this@ActivityPackagesSelection, "")
            p.launchCardPayment(req, PaymentResponseCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PaymentResponseCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    onCardPaymentResponse(CardPaymentData.getFromIntent(data!!))
                } else if (requestCode == Activity.RESULT_CANCELED) {
                    Log.d("CardPaymentData", "RESULT_CANCELED")
                }
            }
        }
    }

    fun onCardPaymentResponse(data: CardPaymentData) {
        when (data.code) {
            CardPaymentData.STATUS_PAYMENT_AUTHORIZED, CardPaymentData.STATUS_PAYMENT_CAPTURED -> {
                Log.d("CardPaymentData", "STATUS_PAYMENT_CAPTURED")
                Log.d("Subscription", Gson().toJson(GlobalData.packagesOptions))
                helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
                subscriptionApiCall()
            }
            CardPaymentData.STATUS_PAYMENT_FAILED -> {
                Log.d("CardPaymentData", "STATUS_PAYMENT_FAILED")
                helperMethods.showAlertDialog(this, object : alertActionClickListner {
                    override fun onActionOk() {
                    }

                    override fun onActionCancel() {
                    }
                }, getString(R.string.payment_), getString(R.string.payment_failed), true, getString(R.string.ok), "")
            }
            CardPaymentData.STATUS_GENERIC_ERROR -> {
                Log.d("CardPaymentData", "STATUS_GENERIC_ERROR")
                helperMethods.showAlertDialog(this, object : alertActionClickListner {
                    override fun onActionOk() {
                    }

                    override fun onActionCancel() {
                    }
                }, getString(R.string.payment_), getString(R.string.Payment_generic_error), true, getString(R.string.ok), "")
            }
            else -> {
                Log.d("CardPaymentData", "unknown")
            }
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
        val referralAmount = (TransactionAmount / 100.0f) * referralResponse.data.points.toDouble()
        TransactionAmount -= referralAmount
        totelDiscountAmount += GlobalData.packagesOptions.discount.toDouble()
        totelDiscountAmount += referralAmount
        val vatAmount = TransactionAmount * 0.00
        TransactionAmount += vatAmount

        GlobalData.packagesOptions.transaction_amount = helperMethods.convetDecimalFormat(TransactionAmount)
        GlobalData.packagesOptions.vat_amount = helperMethods.convetDecimalFormat(vatAmount)

        if (referralResponse.data.points.toDouble() > 0) {
            GlobalData.packagesOptions.referral = "true"
            GlobalData.packagesOptions.referral_code = referralResponse.data.referral_code
            GlobalData.packagesOptions.referral_percent = referralResponse.data.points
            GlobalData.packagesOptions.referral_discount = helperMethods.convetDecimalFormat(referralAmount)
            referralAmountTxt.text = "(" + GlobalData.packagesOptions.referral_percent + " %)  " + GlobalData.packagesOptions.referral_discount + " " + getString(R.string.usd)
        } else {
            GlobalData.packagesOptions.referral = "false"
            GlobalData.packagesOptions.referral_code = ""
            GlobalData.packagesOptions.referral_percent = "0"
            GlobalData.packagesOptions.referral_discount = "0"
        }


        chooseName.text = GlobalData.packagesOptions.name
        Log.d("jawad", GlobalData.packagesOptions.transaction_amount)
        choosePrice.text = GlobalData.packagesOptions.transaction_amount
        originalPrice.text = resources.getString(R.string.usd) + " " + GlobalData.packagesOptions.originalPrice



        chooseDiscount.text = "- " + resources.getString(R.string.usd) + " " + helperMethods.convetDecimalFormat(totelDiscountAmount) //  vatAmountTxt.text = "${resources.getString(R.string.usd)} ${GlobalData.packagesOptions.vat_amount} ${resources.getString(R.string.vat_5)}"
        if (totelDiscountAmount > 0) {
            chooseDiscount.visibility = View.VISIBLE
        } else {
            chooseDiscount.visibility = View.INVISIBLE
        }
        chooseType.text = when (GlobalData.packagesOptions.type) {
            "course" -> {
                getString(R.string.courses)
            }
            "consultation" -> {
                getString(R.string.consultants)
            }
            "subscription" -> {
                getString(R.string.packages)
            }
            else -> ""
        }

        if (!referralResponse.data.points.equals("0")) {
            referralDiscountLayout.visibility = View.VISIBLE
        } else {
            referralDiscountLayout.visibility = View.GONE
        }
        Log.d("payment amount", GlobalData.packagesOptions.transaction_amount + "  " + GlobalData.packagesOptions.vat_amount + "  " + GlobalData.packagesOptions.discount + "  " + GlobalData.packagesOptions.referral_discount)
        if (referralResponse.android_pay == "2") {
            proceed.text = getString(R.string.Proceed_with_cash)
        } else {
            proceed.text = getString(R.string.proceed_payment)
        }
    }

    fun showCouponAddPopup() {
        val LayoutView = LayoutInflater.from(this@ActivityPackagesSelection).inflate(R.layout.add_coupon_layout, null)
        val aleatdialog = AlertDialog.Builder(this@ActivityPackagesSelection)
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
                            Log.d("refral_api Response",referralResponse.toString())
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
        Log.d("Subscription", Gson().toJson(GlobalData.packagesOptions))
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
        val responseBodyCall = retrofitInterface.USER_SUBSCRIPTION_API_CALL("Bearer ${dataUser.access_token}", GlobalData.packagesOptions.type, GlobalData.packagesOptions.category_id, GlobalData.packagesOptions.chat, GlobalData.packagesOptions.audio, GlobalData.packagesOptions.video, subscription_id, course_id, consultant_id, GlobalData.packagesOptions.transaction_amount, "0", "1", if (referralResponse.android_pay == "2") {
            UUID.randomUUID().toString()
        } else {
            paymentNetworkResponse._embedded.payment.get(0).orderReference
        }, GlobalData.packagesOptions.coupon_id, GlobalData.packagesOptions.coupon_code, GlobalData.packagesOptions.discount, GlobalData.packagesOptions.referral_code, GlobalData.packagesOptions.referral_discount, GlobalData.packagesOptions.referral_percent)

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

                                 if(condition=="1"){
                                     saveAppointmentApiCall(consultantId, appointmentDate, appointmentTime, categoryId)

                                 }else{
                                     startActivity(Intent(this@ActivityPackagesSelection, ThanksPageActivity::class.java))
                                 }
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

    fun saveAppointmentApiCall(consultant_id: String, data: String, time: String, categoryId: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.SAVE_APPOINTMENT_API_CALL("Bearer ${dataUser.access_token}", consultant_id, data, time, categoryId)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {

                                startActivity(Intent(this@ActivityPackagesSelection, ThanksPageActivity::class.java))

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

}