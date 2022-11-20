package estisharatibussiness.users.com.UserInterfaces

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import estisharatibussiness.users.com.AdapterClasses.OnBoardingPagerAdapter
import estisharatibussiness.users.com.DataClassHelperMehtods.Boarding.DataBoarding
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoarding : BaseCompatActivity() {
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
        helperMethods = HelperMethods(this@OnBoarding)

        helperMethods.setStatusBarColor(this, R.color.white)
        handler = Handler()
    }

    fun clickEvents() {
        skip.setOnClickListener {
            startActivity(Intent(this@OnBoarding, UserDrawerActivity::class.java))
            finish()
        }
    }

    private fun ShowViewPager() {
        val boardingArrayList: ArrayList<DataBoarding> = arrayListOf()
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_1, getString(R.string.online_cources), getString(R.string.browse_now_hundreds_of_e_courses_in_all_fields_learn_now)))
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_2, getString(R.string.chat), getString(R.string.chat_and_speak_with_members_andconsultants_freely_through_audio_and_video)))
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_3, getString(R.string.ask_for_advice), getString(R.string.we_have_consultants_in_all_fields_we_are_here_to_help_you_start_now)))
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
