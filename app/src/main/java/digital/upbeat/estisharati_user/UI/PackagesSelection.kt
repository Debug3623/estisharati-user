package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_packages_selection.*
import kotlinx.android.synthetic.main.add_coupon_layout.view.*

class PackagesSelection : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packages_selection)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@PackagesSelection)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        change_packages.setOnClickListener { finish() }
        proceed.setOnClickListener { startActivity(Intent(this@PackagesSelection, PaymentMethods::class.java)) }
        addCouponLayout.setOnClickListener {
            showCouponAddPopup()
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
            helperMethods.showToastMessage("Coupon added successfully !")
            dialog.dismiss()
        }
    }
}