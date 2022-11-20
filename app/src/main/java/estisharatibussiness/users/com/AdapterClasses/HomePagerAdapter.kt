package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.Home.Slider
import estisharatibussiness.users.com.FragmentClasses.Home
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.*
import kotlinx.android.synthetic.main.home_slider_item.view.*
import kotlin.collections.ArrayList

class HomePagerAdapter(var context: Context, val home: Home, val sliderArrayList: ArrayList<Slider>) : PagerAdapter() {
    var layoutInflater: LayoutInflater

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return sliderArrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutView = layoutInflater.inflate(R.layout.home_slider_item, container, false) as View
        Glide.with(context).load(sliderArrayList.get(position).image_path).apply(home.helperMethods.requestOption).into(layoutView.sliderImage)
        layoutView.sliderTitle.text = sliderArrayList.get(position).title
        layoutView.sliderDescription.text = home.helperMethods.getHtmlText(sliderArrayList.get(position).description).toString()
        layoutView.sliderDiscover.setOnClickListener {
            when (sliderArrayList.get(position).type) {
                "external_link" -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sliderArrayList.get(position).url))
                    context.startActivity(intent)
                }
                "course" -> {
                    val intent = Intent(context, ActivityCourseDetails::class.java)
                    intent.putExtra("courseId", sliderArrayList.get(position).course_id)
                    context.startActivity(intent)
                }
                "consultant" -> {
                    val intent = Intent(context, ActivityConsultantDetails::class.java)
                    intent.putExtra("consultant_id", sliderArrayList.get(position).consultant_id)
                    intent.putExtra("category_id", "")
                    context.startActivity(intent)
                }
                "package" -> {
                    val intent = Intent(context, ActivityPackages::class.java)
                    intent.putExtra("package_id", sliderArrayList.get(position).package_id)
                    intent.putExtra("viaFrom", "Home")
                    context.startActivity(intent)
                }
                "ask_for_your_advice_here" -> {
                    val intent = Intent(context, ActivityLegalAdvice::class.java)
                    intent.putExtra("category_id", "")
                    intent.putExtra("category_name", "")
                    context.startActivity(intent)
                }
                "our_courses" -> {
                    val intent = Intent(context, ActivityPackages::class.java)
                    intent.putExtra("viaFrom", "Home")
                    context.startActivity(intent)
                }
                "analyze_your_personality" -> {
                    context.startActivity(Intent(context, ActivitySurveyList::class.java))
                }
                "show_only" -> {
                }
            }
        }

        if (sliderArrayList.get(position).type.equals("show_only")) {
            layoutView.sliderDiscover.visibility = View.GONE
        } else {
            layoutView.sliderDiscover.visibility = View.VISIBLE
        }


        container.addView(layoutView)
        return layoutView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}