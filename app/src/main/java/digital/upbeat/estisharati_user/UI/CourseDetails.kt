package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import digital.upbeat.estisharati_user.Adapter.TapViewPagerAdapter
import digital.upbeat.estisharati_user.Fragment.Comments
import digital.upbeat.estisharati_user.Fragment.CourseContent
import digital.upbeat.estisharati_user.Fragment.Instructor
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_course_details.*
import kotlinx.android.synthetic.main.tap_item.view.*

class CourseDetails : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_details)
        initViews()
        clickEvents()
        tapInitialize()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@CourseDetails)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        show_more.setOnClickListener { helperMethods.AlertPopup("Course description", "Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern societyLearn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society Learn about the tasks and functions of human tasks in the labor market and keep pace with development Modern society") }
        preview_course_img.setOnClickListener {
            startActivity(Intent(this@CourseDetails, PreviewCourse::class.java))
        }
    }

    fun tapInitialize() {
        val tabOne = layoutInflater.inflate(R.layout.tap_item, null) as View
        val tabTwo = layoutInflater.inflate(R.layout.tap_item, null) as View
        val tabThree = layoutInflater.inflate(R.layout.tap_item, null) as View
        tabOne.tap_text.text = "Course content"
        tabTwo.tap_text.text = "Instructor"
        tabThree.tap_text.text = "Comments"
        setUpViewPager(course_viewpager)
        course_tablayout.setupWithViewPager(course_viewpager)
        course_tablayout.getTabAt(0)?.setCustomView(tabOne)
        course_tablayout.getTabAt(1)?.setCustomView(tabTwo)
        course_tablayout.getTabAt(2)?.setCustomView(tabThree)
        //        val linearLayout = search_tabLayout.getChildAt(0) as LinearLayout
        //        linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        //        val drawable = GradientDrawable()
        //        drawable.setColor(resources.getColor(R.color.gainsboro_gray))
        //        drawable.setSize(2, 1)
        //        linearLayout.setDividerPadding(20);
        //        linearLayout.dividerDrawable = drawable
        //        tabOne.setBackgroundColor(getResources().getColor(R.color.lighter_white));
        //        search_tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        //            @Override
        //            public void onTabSelected(TabLayout.Tab tab) {
        //                if (tab.getPosition() == 0) {
        //                    tabOne.setBackgroundColor(getResources().getColor(R.color.lighter_white));
        //                } else {
        //                    tabTwo.setBackgroundColor(getResources().getColor(R.color.lighter_white));
        //                }
        //
        //            }
        //
        //            @Override
        //            public void onTabUnselected(TabLayout.Tab tab) {
        //
        //            }
        //
        //            @Override
        //            public void onTabReselected(TabLayout.Tab tab) {
        //
        //            }
        //        });
        //                course_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        //                    override fun onTabReselected(tab: TabLayout.Tab?) {
        //                    }
        //
        //                    override fun onTabUnselected(tab: TabLayout.Tab?) {
        //                    }
        //
        //                    override fun onTabSelected(tab: TabLayout.Tab?) {
        //                        course_viewpager.reMeasureCurrentPage(tab!!.position)
        //
        //                    }
        //                })
    }

    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = TapViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(CourseContent(), "ONE")
        adapter.addFrag(Instructor(), "TWO")
        adapter.addFrag(Comments(), "THREE")
        viewPager.setAdapter(adapter)
    }
}