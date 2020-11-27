package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.PaymentMethodAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.alertActionClickListner
import kotlinx.android.synthetic.main.activity_payment_methods.*

class PaymentMethods : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@PaymentMethods)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        proceed.setOnClickListener {
            startActivity(Intent(this@PaymentMethods, ThanksPage::class.java))
        }
        add_payment_method.setOnClickListener {
            val intent = Intent(this@PaymentMethods, AddPaymentMethod::class.java)
            intent.putExtra( "click_from","add")
            intent.putExtra( "card_paypal","")
            startActivity(intent)
        }
    }

    fun InitializeRecyclerview() {
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")


        payment_method_recycler.setHasFixedSize(true)
        payment_method_recycler.removeAllViews()
        payment_method_recycler.layoutManager = LinearLayoutManager(this@PaymentMethods)
        payment_method_recycler.adapter = PaymentMethodAdapter(this@PaymentMethods, this@PaymentMethods, arrayList)
    }

    fun cardAccountRemovePopup(messageStr: String) {
        helperMethods.showAlertDialog(this@PaymentMethods, object : alertActionClickListner {
            override fun onActionOk() {
            }

            override fun onActionCancel() {
            }
        }, "Remove", messageStr, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))


    }
}