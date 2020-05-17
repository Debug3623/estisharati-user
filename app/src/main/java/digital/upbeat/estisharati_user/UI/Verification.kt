package digital.upbeat.estisharati_user.UI

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.PinOnKeyListener
import digital.upbeat.estisharati_user.Utils.PinTextWatcher
import kotlinx.android.synthetic.main.activity_verification.*

class Verification : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    var resendTimer: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Verification);
        if (intent.extras != null) {
            val come_from = intent.getStringExtra("come_from")
            Log.d("come_from", come_from + "")
        }
    }

    fun clickEvents() {
        nav_back.setOnClickListener {
            finish()
        }
        val editTexts = arrayOf(code_1, code_2, code_3, code_4)

        code_1.addTextChangedListener(PinTextWatcher(this@Verification, 0, editTexts))
        code_2.addTextChangedListener(PinTextWatcher(this@Verification, 1, editTexts))
        code_3.addTextChangedListener(PinTextWatcher(this@Verification, 2, editTexts))
        code_4.addTextChangedListener(PinTextWatcher(this@Verification, 3, editTexts))

        code_1.setOnKeyListener(PinOnKeyListener(this@Verification, 0, editTexts))
        code_2.setOnKeyListener(PinOnKeyListener(this@Verification, 1, editTexts))
        code_3.setOnKeyListener(PinOnKeyListener(this@Verification, 2, editTexts))
        code_4.setOnKeyListener(PinOnKeyListener(this@Verification, 3, editTexts))

        btn_proceed.setOnClickListener {
            Toast.makeText(this@Verification, "OTP verified!", Toast.LENGTH_LONG).show()
            val code = "${code_1.text.toString()}${code_2.text.toString()}${code_3.text.toString()}${code_4.text.toString()}"
            val result = Intent()
            result.putExtra("value1", "hi")
            result.putExtra("value2", "how")
            result.putExtra("value3", "are you? $code")
            setResult(Activity.RESULT_OK, result);
            finish()
        }

        retry.setOnClickListener { ResendCountdown() }
    }

    fun ResendCountdown() {
        retry.visibility = View.GONE
        retry_on.visibility = View.VISIBLE
        if (resendTimer != null) {
            resendTimer?.cancel()
        }
        resendTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                retry_on.text = "Retry on ${helperMethods.MillisUntilToTime(millisUntilFinished)}"
            }

            override fun onFinish() {
                retry.visibility = View.VISIBLE
                retry_on.visibility = View.GONE
            }
        }
        resendTimer?.start()
    }
}
