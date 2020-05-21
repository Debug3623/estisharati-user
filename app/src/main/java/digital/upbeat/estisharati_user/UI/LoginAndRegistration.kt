package digital.upbeat.estisharati_user.UI

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_login_and_registration.*

class LoginAndRegistration : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    var eyeVisible: Boolean = false
    var eyeVisibleReg: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_and_registration)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@LoginAndRegistration);

        helperMethods.setStatusBarColor(this, R.color.white)
    }

    fun clickEvents() {
        if (eyeVisible) {
            eye.setBackgroundResource(R.drawable.ic_eye_invisible)
            password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            eye.setBackgroundResource(R.drawable.ic_eye_visible)
            password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        eye.setOnClickListener {
            if (eyeVisible) {
                eyeVisible = false
                eye.setBackgroundResource(R.drawable.ic_eye_visible)
                password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                eyeVisible = true
                eye.setBackgroundResource(R.drawable.ic_eye_invisible)
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }
        if (eyeVisibleReg) {
            reg_eye.setBackgroundResource(R.drawable.ic_eye_invisible)
            reg_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            reg_eye.setBackgroundResource(R.drawable.ic_eye_visible)
            reg_password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        reg_eye.setOnClickListener {
            if (eyeVisibleReg) {
                eyeVisibleReg = false
                reg_eye.setBackgroundResource(R.drawable.ic_eye_visible)
                reg_password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                eyeVisibleReg = true
                reg_eye.setBackgroundResource(R.drawable.ic_eye_invisible)
                reg_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        nav_login.setOnClickListener {
            login_layout.visibility = View.VISIBLE
            register_layout.visibility = View.GONE
        }
        btn_nav_login.setOnClickListener {
            login_layout.visibility = View.VISIBLE
            register_layout.visibility = View.GONE
        }

        nav_register.setOnClickListener {
            login_layout.visibility = View.GONE
            register_layout.visibility = View.VISIBLE
        }
        btn_nav_register.setOnClickListener {
            login_layout.visibility = View.GONE
            register_layout.visibility = View.VISIBLE
        }
        register.setOnClickListener {
            val intent = Intent(this@LoginAndRegistration, Verification::class.java)
            intent.putExtra("come_from", "LoginAndRegistration")
            startActivityForResult(intent, 123)
        }
        nav_forget_password.setOnClickListener { startActivity(Intent(this@LoginAndRegistration, ForgotPassword::class.java)) }
        btn_login.setOnClickListener {
            startActivity(Intent(this@LoginAndRegistration, UserDrawer::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                val street = data!!.getStringExtra("value1")
                val city = data.getStringExtra("value2")
                val home = data.getStringExtra("value3")
                Log.d("result", "$street $city $home")
                startActivity(Intent(this@LoginAndRegistration,OnBoarding::class.java))
                finish()
            }
        }
    }
}
