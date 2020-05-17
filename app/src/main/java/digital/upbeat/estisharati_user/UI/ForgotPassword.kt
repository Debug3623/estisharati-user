package digital.upbeat.estisharati_user.UI

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ForgotPassword);
        helperMethods.setStatusBarColor(this, R.color.white)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        btn_nav_login.setOnClickListener { finish() }
        send_code.setOnClickListener {
            Toast.makeText(this@ForgotPassword,"OTP sent to you email address!", Toast.LENGTH_LONG).show()

            val intent = Intent(this@ForgotPassword, Verification::class.java)
            intent.putExtra("come_from", "ForgotPassword")
            startActivityForResult(intent, 123)
        }
        save_passwrod.setOnClickListener {
            Toast.makeText(this@ForgotPassword,"Password changes successfully",Toast.LENGTH_LONG).show()
            finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                val street = data!!.getStringExtra("value1")
                val city = data.getStringExtra("value2")
                val home = data.getStringExtra("value3")
                Log.d("result", "$street $city $home")
                forgot_password_layout.visibility=View.GONE
                change_password_layot.visibility=View.VISIBLE
            }
        }
    }
}
