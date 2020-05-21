package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import digital.upbeat.estisharati_user.Adapter.*
import digital.upbeat.estisharati_user.DataClassHelper.DataBoarding
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.CirclePageIndicator
import kotlinx.android.synthetic.main.fragment_home.*

class Home : Fragment() {
    lateinit var helperMethods: HelperMethods
    var homePagerAdapter: HomePagerAdapter? = null
    var count = 0
    private var handler: Handler = Handler()
    val delay: Long = 3000
    var runnable: Runnable = object : Runnable {
        override fun run() {
            if (count == homePagerAdapter!!.count - 1) {
                count = 0
            } else {
                count++
            }
            viewpager.setCurrentItem(count, true)
            handler.postDelayed(this, delay.toLong())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        clickEvents()
        ShowViewPager()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(requireContext())
    }

    fun clickEvents() {}
    private fun ShowViewPager() {
        val boardingArrayList: ArrayList<DataBoarding> = arrayListOf()
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_1, "Online Cources", "Browse Now hundreds of e-courses\nIn all fields .. learn now"))
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_2, "Chat", "Chat and speak with members and\nconsultants Freely through audio and video"))
        boardingArrayList.add(DataBoarding(R.drawable.ic_onboard_3, "Ask for advice", "We have consultants in all fields\nWe are here to help you .. start now"))
        homePagerAdapter = HomePagerAdapter(requireContext(), boardingArrayList)
        val rotateimage = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in)
        viewpager.adapter = homePagerAdapter
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

    fun InitializeRecyclerview() {
        var arrayList: ArrayList<String> = arrayListOf()

        arrayList.add("Lifestyle")
        arrayList.add("Health & fitness")
        arrayList.add("Finance and Accounting")
        arrayList.add("Money & business")
        arrayList.add("Law")
        arrayList.add("Marketing")
        arrayList.add("Design")
        arrayList.add("Human Development")
        arrayList.add("Lifestyle")
        arrayList.add("Health & fitness")

        exp_consultations_recycler.setHasFixedSize(true)
        exp_consultations_recycler.removeAllViews()
        exp_consultations_recycler.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        exp_consultations_recycler.adapter = ExpConsultationsAdapter(requireContext(), this, arrayList)
        arrayList= arrayListOf()
        arrayList.add("Finance and Accounting")
        arrayList.add("Money & business")
        arrayList.add("Law")
        arrayList.add("Marketing")
        arrayList.add("Design")
        arrayList.add("Human Development")
        arrayList.add("Lifestyle")
        arrayList.add("Health & fitness")
        arrayList.add("Finance and Accounting")
        arrayList.add("Money & business")
        arrayList.add("Law")
        arrayList.add("Marketing")
        arrayList.add("Design")
        arrayList.add("Human Development")

        exp_consultations_recycler1.setHasFixedSize(true)
        exp_consultations_recycler1.removeAllViews()
        exp_consultations_recycler1.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        exp_consultations_recycler1.adapter = ExpConsultationsAdapter(requireContext(), this, arrayList)

         exp_courses_recycler.setHasFixedSize(true)
        exp_courses_recycler.removeAllViews()
        exp_courses_recycler.layoutManager = LinearLayoutManager(requireContext(),  LinearLayoutManager.HORIZONTAL, false)
        exp_courses_recycler.adapter = ExpCoursesAdapter(requireContext(), this, arrayList)

        online_user_recycler.setHasFixedSize(true)
        online_user_recycler.removeAllViews()
        online_user_recycler.layoutManager = LinearLayoutManager(requireContext(),  LinearLayoutManager.HORIZONTAL, false)
        online_user_recycler.adapter = OnlineUserAdapter(requireContext(), this, arrayList)


    }
}
