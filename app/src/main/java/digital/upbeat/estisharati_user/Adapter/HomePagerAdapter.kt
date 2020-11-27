package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.DataBoarding
import digital.upbeat.estisharati_user.DataClassHelper.Home.Slider
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_course_details.*
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
        layoutView.sliderDescription.text = sliderArrayList.get(position).description
        layoutView.sliderDiscover.setOnClickListener { }
        container.addView(layoutView)
        return layoutView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}