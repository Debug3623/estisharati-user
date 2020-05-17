package digital.upbeat.estisharati_user.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R

class SplashScreen : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        initViews()
        startCountDownTimer()
    }


    fun initViews() {
        helperMethods = HelperMethods(this@SplashScreen);

        helperMethods.setStatusBarColor(this, R.color.white)
    }

    fun startCountDownTimer(){
        object :CountDownTimer(2000,1000){
            override fun onFinish() {
                startActivity(Intent(this@SplashScreen,LoginAndRegistration::class.java))
          finish()
            }
            override fun onTick(millisUntilFinished: Long) {
            }
        }.start()
    }
}
