package estisharatibussiness.users.com.UserInterface

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.gson.Gson
import estisharatibussiness.users.com.ApiHelper.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelper.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelper.Login.DataUser
import estisharatibussiness.users.com.DataClassHelper.PaymentMethodList.Data
import estisharatibussiness.users.com.DataClassHelper.PaymentRequest.Details
import estisharatibussiness.users.com.DataClassHelper.PaymentRequest.PaymentRequest
import estisharatibussiness.users.com.DataClassHelper.PaymentType.PTResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_add_payment_method.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.regex.Pattern

class ActivityAddPaymentActivity : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    var ptResponse: PTResponse = PTResponse(arrayListOf(), "","")
    var paymentMethodType = "1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_payment_method)
        initViews()
        clickEvents()
        creditCardTextChanged()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityAddPaymentActivity)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ActivityAddPaymentActivity)
        dataUser = sharedPreferencesHelper.logInUser

        if (helperMethods.isConnectingToInternet) {
            paymentTypeApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun clickEvents() {
        if (intent.extras != null) {
            val click_from = intent.getStringExtra("click_from")
            val card_paypal = intent.getStringExtra("card_paypal")
            if (intent.hasExtra("payment_mehtod")) {
                val paymentMehtod = intent.getParcelableExtra<Data>("payment_mehtod") as Data
                paymentMethodType = paymentMehtod.payment_method!!
                if (paymentMehtod.payment_method.equals("1")) {
                    credit_card_number.text = paymentMehtod.details!!.cardno!!.toEditable()
                    name_on_card.text = paymentMehtod.details.name!!.toEditable()
                    card_cvv.text = paymentMehtod.details.cvv!!.toEditable()
                    val expireDate = paymentMehtod.details.expiry!!.split("/")
                    card_month.text = expireDate.get(0).toEditable()
                    card_year.text = expireDate.get(1).toEditable()
                    paypal_email.text = paymentMehtod.details.email!!.toEditable()
                    cardFromat()
                } else if (paymentMehtod.payment_method.equals("2")) {
                    paypal_email.text = paymentMehtod.details!!.email!!.toEditable()
                }
            }
            if (click_from.equals("add")) {
                action_bar_title.text = "Add Payment Method"
                submit_card.text = "Save"
                submit_paypal.text = "Save"
            } else if (click_from.equals("update")) {
                action_bar_title.text = "Update Payment Method"
                if (card_paypal.equals("card")) {
                    submit_card.text = "Update"
                    submit_paypal.text = "Save"

                    paypal_account_layout.visibility = View.GONE
                    credit_card_layout.visibility = View.VISIBLE

                    credit_card_indicater.visibility = View.VISIBLE
                    paypal_indicater.visibility = View.GONE
                } else if (card_paypal.equals("paypal")) {
                    submit_paypal.text = "Update"
                    submit_card.text = "Save"

                    paypal_account_layout.visibility = View.VISIBLE
                    credit_card_layout.visibility = View.GONE

                    paypal_indicater.visibility = View.VISIBLE
                    credit_card_indicater.visibility = View.GONE
                }
            }
        }

        submit_paypal.setOnClickListener {
            if (!helperMethods.isvalidEmail(paypal_email.toText())) {
                helperMethods.showToastMessage("Enter valid email address")
                return@setOnClickListener
            }

            if (!helperMethods.isConnectingToInternet) {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                return@setOnClickListener
            }
            val details = Details("", "", paypal_email.toText(), "", "")
            val PaymentRequest = PaymentRequest(details, paymentMethodType)
            addPaymentApiCall(PaymentRequest)
        }
        submit_card.setOnClickListener {
            if (validateCardDetails()) {
                val cardNumber = credit_card_number.toText().replace("-", "")
                val details = Details(cardNumber, card_cvv.toText(), paypal_email.toText(), card_month.toText() + "/" + card_year.toText(), name_on_card.toText())
                val PaymentRequest = PaymentRequest(details, paymentMethodType)
                addPaymentApiCall(PaymentRequest)
            }
        }
        nav_back.setOnClickListener { finish() }
        credit_card.setOnClickListener {
            paypal_account_layout.visibility = View.GONE
            credit_card_layout.visibility = View.VISIBLE

            credit_card_indicater.visibility = View.VISIBLE
            paypal_indicater.visibility = View.GONE
            credit_card_number.text = "".toEditable()
            name_on_card.text = "".toEditable()
            card_cvv.text = "".toEditable()
            card_month.text = "".toEditable()
            card_year.text = "".toEditable()
            paypal_email.text = "".toEditable()
            paymentMethodType = "1"
        }
        paypal_account.setOnClickListener {
            paypal_account_layout.visibility = View.VISIBLE
            credit_card_layout.visibility = View.GONE

            paypal_indicater.visibility = View.VISIBLE
            credit_card_indicater.visibility = View.GONE
            paypal_email.text = "".toEditable()
            paymentMethodType = "2"
        }
    }

    fun creditCardTextChanged() {
        credit_card_number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // noop
            }

            override fun onTextChanged(s: CharSequence, st: Int, be: Int, count: Int) {
                cardFromat()
            } // remove everything but numbers

            override fun afterTextChanged(s: Editable) {
                if (credit_card_number.text.toString().length == 19) {
                    card_cvv.requestFocus()
                }
            }
        })


        card_cvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (card_cvv.length() == 3) {
                    card_month.requestFocus()
                }
            }
        })
        card_month.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (card_month.length() == 2) {
                    card_year.requestFocus()
                }
            }
        })

        card_year.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (card_year.length() == 4) {
                }
            }
        })
    }

    fun cardFromat() {
        val space = "-" // you can change this to whatever you want
        val pattern = Pattern.compile("^(\\d{4}$space{1}){0,3}\\d{1,4}$") // check whether we need to modify or not
        val currentText = credit_card_number.text.toString()
        if (currentText.isEmpty() || pattern.matcher(currentText).matches()) return  // no need to modify
        val numbersOnly = currentText.trim { it <= ' ' }.replace("[^\\d.]".toRegex(), "")
        var formatted = ""
        var i = 0
        while (i < numbersOnly.length) {
            if (i + 4 < numbersOnly.length) formatted += numbersOnly.substring(i, i + 4) + space
            else formatted += numbersOnly.substring(i)
            i += 4
        }
        credit_card_number.setText(formatted)
        credit_card_number.setSelection(credit_card_number.text.toString().length)
    }

    fun paymentTypeApiCall() {
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.PAYMENTMETHOD_TYPE_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            ptResponse = Gson().fromJson(response.body()!!.string(), PTResponse::class.java)
                            if (ptResponse.status.equals("200")) {
                                checkPaymentType()
                            } else {
                            if (helperMethods.checkTokenValidation(ptResponse.status, ptResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup("Alert", ptResponse.message)
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

    fun checkPaymentType() {
        var cardStatus = false
        var paypalStatus = false
        for (index in ptResponse.data) {
            if (index.payment_method.equals("card_payment")) cardStatus = true
            else if (index.payment_method.equals("paypal")) paypalStatus = true
            else {
            }
        }
        Log.d("HANE", cardStatus.toString() + " " + paypalStatus.toString())

        if (!cardStatus) {
            credit_card.visibility = View.GONE
            credit_card_layout.visibility = View.GONE
        }
        if (!paypalStatus) {
            paypal_account.visibility = View.GONE
            paypal_account_layout.visibility = View.GONE
        }
    }

    fun validateCardDetails(): Boolean {
        if (name_on_card.toText().equals("")) {
            helperMethods.showToastMessage("please enter card holder name")
            return false
        }
        if (credit_card_number.toText().equals("")) {
            helperMethods.showToastMessage("please enter card number")
            return false
        }
        if (credit_card_number.toText().length != 19) {
            helperMethods.showToastMessage("please enter valid card number")
            return false
        }
        if (card_cvv.toText().length != 3) {
            helperMethods.showToastMessage("please enter card CVV")
            return false
        }
        if (card_month.toText().length != 2) {
            helperMethods.showToastMessage("please enter card month")
            return false
        }
        if (card_year.toText().length != 4) {
            helperMethods.showToastMessage("please enter card year")
            return false
        }
        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun addPaymentApiCall(paymentRequest: PaymentRequest) {
        Log.d("PaymentRequest", Gson().toJson(paymentRequest))

        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.ADD_PAYMENT_API_CALL("Bearer ${dataUser.access_token}", paymentRequest)
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
                                finish()
                            } else {
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    finish()
                                    return
                                }
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

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}