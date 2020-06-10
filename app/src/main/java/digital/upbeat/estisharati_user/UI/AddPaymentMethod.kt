package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_add_payment_method.*
import kotlinx.android.synthetic.main.activity_add_payment_method.action_bar_title
import kotlinx.android.synthetic.main.app_bar_user_drawer.*
import java.util.regex.Pattern

class AddPaymentMethod : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_payment_method)
        initViews()
        clickEvents()
        creditCardTextChanged()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@AddPaymentMethod)
    }

    fun clickEvents() {
        if (intent.extras != null) {
            val click_from = intent.getStringExtra("click_from")
            val card_paypal = intent.getStringExtra("card_paypal")
            if (click_from.equals("add")) {
                action_bar_title.text = "Add Payment Method"
                submit_card.text = "Save"
                submit_paypal.text = "Save"
            } else {
                action_bar_title.text = "Update Payment Method"
                if (card_paypal.equals("card")) {
                    submit_card.text = "Update"
                    submit_paypal.text = "Save"

                    paypal_account_layout.visibility = View.GONE
                    credit_card_layout.visibility = View.VISIBLE

                    credit_card_indicater.visibility = View.VISIBLE
                    paypal_indicater.visibility = View.GONE

                } else {
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
            finish()
        }
        submit_card.setOnClickListener {
            finish()
        }
        nav_back.setOnClickListener { finish() }
        credit_card.setOnClickListener {
            paypal_account_layout.visibility = View.GONE
            credit_card_layout.visibility = View.VISIBLE

            credit_card_indicater.visibility = View.VISIBLE
            paypal_indicater.visibility = View.GONE
        }
        paypal_account.setOnClickListener {
            paypal_account_layout.visibility = View.VISIBLE
            credit_card_layout.visibility = View.GONE

            paypal_indicater.visibility = View.VISIBLE
            credit_card_indicater.visibility = View.GONE
        }
    }

    fun creditCardTextChanged() {
        credit_card_number.addTextChangedListener(object : TextWatcher {
            private val space = "-" // you can change this to whatever you want
            private val pattern = Pattern.compile("^(\\d{4}$space{1}){0,3}\\d{1,4}$") // check whether we need to modify or not
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // noop
            }

            override fun onTextChanged(s: CharSequence, st: Int, be: Int, count: Int) {
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
}