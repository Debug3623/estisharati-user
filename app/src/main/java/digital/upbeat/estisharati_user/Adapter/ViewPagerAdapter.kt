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
import digital.upbeat.estisharati_user.DataClassHelper.DataBoarding
import digital.upbeat.estisharati_user.R
import kotlin.collections.ArrayList

class ViewPagerAdapter(var context: Context, val boardingArrayList: ArrayList<DataBoarding>) : PagerAdapter() {
    var layoutInflater: LayoutInflater

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return boardingArrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutView = layoutInflater.inflate(R.layout.boarding_item, container, false) as View
        val image = layoutView.findViewById(R.id.image) as ImageView
        val title = layoutView.findViewById(R.id.title) as TextView
        val message = layoutView.findViewById(R.id.message) as TextView
        image.setImageResource(boardingArrayList.get(position).image)
        title.text = boardingArrayList.get(position).title
        message.text = boardingArrayList.get(position).message
        container.addView(layoutView)
        return layoutView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}