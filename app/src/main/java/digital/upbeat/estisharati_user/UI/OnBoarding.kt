package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import digital.upbeat.estisharati_user.Adapter.OnBoardingPagerAdapter
import digital.upbeat.estisharati_user.DataClassHelper.DataBoarding
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.CirclePageIndicator
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoarding : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    var adapter: OnBoardingPagerAdapter? = null
    var count = 0
     var handler: Handler = Handler()
    val delay: Long = 3000
    var runnable: Runnable = object : Runnable {
        override fun run() {
            if (count == adapter!!.count - 1) {
                count = 0
            } else {
                count++
            }
            viewpager.setCurrentItem(count, true)
            handler.postDelayed(this, delay.toLong())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        initViews()
        clickEvents()
        ShowViewPager()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@OnBoarding);

        helperMethods.setStatusBarColor(this, R.color.white)
        handler = Handler()
    }

    fun clickEvents() {
        skip.setOnClickListener {
            startActivity(Intent(this@OnBoarding, UserDrawer::class.java))
            finish()
        }
    }

    private fun ShowViewPager() {
        val boardingArrayList: ArrayList<DataBoarding> = arrayListOf()
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_1, "Online Cources", "Browse Now hundreds of e-courses\nIn all fields .. learn now"))
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_2, "Chat", "Chat and speak with members and\nconsultants Freely through audio and video"))
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_3, "Ask for advice", "We have consultants in all fields\nWe are here to help you .. start now"))
        adapter = OnBoardingPagerAdapter(this@OnBoarding, boardingArrayList)
        val rotateimage = AnimationUtils.loadAnimation(this, R.anim.slide_in)
        viewpager.adapter = adapter
        indicator.setViewPager(viewpager)
        viewpager.startAnimation(rotateimage)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, delay)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}
