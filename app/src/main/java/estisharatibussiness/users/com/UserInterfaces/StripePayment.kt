package estisharatibussiness.users.com.UserInterfaces

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.cardform.view.CardForm
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
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import kotlinx.android.synthetic.main.activity_stripe_payment.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class StripePayment : AppCompatActivity() {
    private val REQUEST_SCAN = 100
    private val REQUEST_AUTOTEST = 200
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser
    lateinit var referralResponse: ReferralResponse
//    lateinit var paymentNetworkResponse: NetworkResponse
    val PaymentResponseCode = 1996
    var appointmentDate = "0"
    var appointmentTime = "0"
    var consultantId = ""
    var categoryId = ""
    var condition = ""

    @SuppressLint("MissingInflatedId") override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_payment)

        helperMethods = HelperMethods(this@StripePayment)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@StripePayment)
        dataUser = sharedPreferencesHelper.logInUser

        appointmentDate= intent.getStringExtra("appointment_date").toString()
        appointmentTime= intent.getStringExtra("appointment_time").toString()
        consultantId= intent.getStringExtra("consultant_id").toString()
        categoryId= intent.getStringExtra("category_id").toString()
        condition= intent.getStringExtra("condition").toString()

        Log.d("condition",condition)


//        payview.setOnDataChangedListener(object : Payview.OnChangelistener{
//            override fun onChangelistener(payModel: PayModel?, isFillAllComponents: Boolean) {
//
//
////
////                   cardNumber= payModel?.cardNo?.replace(" ","").toString()
////                   expYear= payModel?.cardMonth.toString().trim()
////                   expMonth= payModel?.cardYear.toString().trim()
////                   cVV= payModel?.cardCv.toString().trim()
////                   userName= payModel?.cardOwnerName.toString().toUpperCase()
//
//
//            }
//
//        })
//
//
//        try {
//            payview.setPayOnclickListener(View.OnClickListener {
//
//
//                //            Log.d("card "," $cardNumber")
//                //            Log.d("expMonth ", "$expMonth")
//                //            Log.d("expYear ", "$expYear")
//                //            Log.d("cVV ", "$cVV")
//                //            Log.d("userName ", "$userName")
//
//
//
//            })
//        } catch (e : Exception){
//            println("Exception is handled.")
//            helperMethods.showToastMessage("Please fill all the fields correctly")
//        }





        val cardForm = findViewById<CardForm>(R.id.card_form)

        cardForm.cardRequired(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .cardholderName(CardForm.FIELD_REQUIRED)
            .actionLabel("Purchase")
            .setup(this@StripePayment)

        cardForm.setCardNumberIcon(0)
        cardForm.setCardholderNameIcon(0)

        cardForm.cvvEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD

        btnBuy.setOnClickListener {

            if (cardForm.isValid) {

                Log.d("card_number", cardForm.cardNumber)
                Log.d("name", cardForm.cardholderName)
                Log.d("year", cardForm.expirationYear)
                Log.d("month", cardForm.expirationMonth)
                Log.d("cvv", cardForm.cvv)
                Log.d("transaction_price","${GlobalData.packagesOptions.transaction_amount}")

                paymentApi(cardForm.cardNumber,cardForm.expirationMonth,cardForm.expirationYear,cardForm.cvv,GlobalData.packagesOptions.transaction_amount,"1")
            }else {
                Toast.makeText(this@StripePayment, "Please complete all the fields correctly", Toast.LENGTH_LONG).show();
            }
        }

        scanCard.setOnClickListener {
            onScanPress(this@StripePayment)
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



    fun paymentApi(cardno: String, month: String, year: String, cvv: String, amount: String, payment_method: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.STRIPE_PAYMENT("Bearer ${dataUser.access_token}",cardno, month, year, cvv, amount, payment_method)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val data = jsonObject.getBoolean("data")
                                if (data){
                                    subscriptionApiCall()
                                    helperMethods.showToastMessage(jsonObject.getString("message"))
                                }else{
                                    helperMethods.showToastMessage(jsonObject.getString("message"))
                                }
                            } else {
                                helperMethods.showToastMessage("error try again!!!")
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
        val responseBodyCall = retrofitInterface.USER_SUBSCRIPTION_API_CALL("Bearer ${dataUser.access_token}", GlobalData.packagesOptions.type, GlobalData.packagesOptions.category_id, GlobalData.packagesOptions.chat, GlobalData.packagesOptions.audio, GlobalData.packagesOptions.video, subscription_id, course_id, if(condition=="1" || condition==null ) {consultantId} else { consultant_id}, GlobalData.packagesOptions.transaction_amount, "0", "1", if (referralResponse.android_pay != "2") {
            UUID.randomUUID().toString()
        } else {
//            paymentNetworkResponse._embedded.payment.get(0).orderReference
            UUID.randomUUID().toString()
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
                                    startActivity(Intent(this@StripePayment, ThanksPageActivity::class.java))
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

                                startActivity(Intent(this@StripePayment, ThanksPageActivity::class.java))

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
















    fun onScanPress(v: StripePayment) {
        val scanIntent = Intent(this, CardIOActivity::class.java) // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true) // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true) // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true)
        // default: false
        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, REQUEST_SCAN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCAN) {
            var resultDisplayStr: String
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                val scanResult = data.getParcelableExtra<CreditCard>(CardIOActivity.EXTRA_SCAN_RESULT) // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = """
                Card Number: ${scanResult!!.cardNumber}
                Card exp month: ${scanResult.expiryMonth}
                Card exp year: ${scanResult.expiryYear}
                Card cvv: ${scanResult.cvv}
               
                
                """.trimIndent()
                paymentApi(scanResult.cardNumber,scanResult.expiryMonth.toString(),"20${scanResult.expiryYear}",scanResult.cvv.toString(),GlobalData.packagesOptions.transaction_amount,"1")
                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );
                Log.d("cardNumber","${scanResult!!.redactedCardNumber}")
                if (scanResult.isExpiryValid) {
                    resultDisplayStr += """
                    Expiration Date: ${scanResult.expiryMonth}/${scanResult.expiryYear}
                   
                    """.trimIndent()
                }
                if (scanResult.cvv != null) { // Never log or display a CVV
                    resultDisplayStr += """CVV has ${scanResult.cvv.length} digits.
                        
"""
                }
                if (scanResult.postalCode != null) {
                    resultDisplayStr += """
                    Postal Code: ${scanResult.postalCode}
                    
                    """.trimIndent()
                }

                Log.d("valuesss",resultDisplayStr.toString())
            } else {
                resultDisplayStr = "Scan was canceled."
            } // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        } // else handle other activity results
    }




}