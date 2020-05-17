package digital.upbeat.estisharati_user.UI

import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import digital.upbeat.estisharati_user.Adapter.ViewPagerAdapter
import digital.upbeat.estisharati_user.DataClassHelper.DataBoarding
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.CirclePageIndicator

class OnBoarding : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    var adapter: ViewPagerAdapter? = null
    lateinit var viewPager: ViewPager
    lateinit var mIndicator: CirclePageIndicator
    var count = 0
    private var handler: Handler? = null
    val delay: Long = 3000
    var runnable: Runnable = object : Runnable {
        override fun run() {
            if (count == adapter!!.count - 1) {
                count = 0
            } else {
                count++
            }
            viewPager.setCurrentItem(count, true)
            handler!!.postDelayed(this, delay.toLong())
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
        viewPager = findViewById(R.id.viewpager) as ViewPager
        mIndicator = findViewById(R.id.indicator) as CirclePageIndicator
        handler = Handler();
    }

    fun clickEvents() {}
    private fun ShowViewPager() {
        //        ArrayList images_array = shared_preferences.GET_SHARED_TUTORIAL_IMAGES_ARRALIST();
        val boardingArrayList: ArrayList<DataBoarding> = arrayListOf()
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_1, "Online Cources", "Browse Now hundreds of e-courses\nIn all fields .. learn now"))
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_2, "Chat", "Chat and speak with members and consultants\nFreely through audio and video"))
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_3, "Ask for advice", "We have consultants in all fields\nWe are here to help you .. start now"))
        adapter = ViewPagerAdapter(this@OnBoarding, boardingArrayList)
        val rotateimage = AnimationUtils.loadAnimation(this, R.anim.slide_in)
        viewPager.adapter = adapter
        mIndicator.setViewPager(viewPager)
        viewPager.startAnimation(rotateimage)
    }

    override fun onResume() {
        super.onResume()
        handler?.postDelayed(runnable, delay)
    }

    override fun onPause() {
        super.onPause()
        handler?.removeCallbacks(runnable)
    }
}
