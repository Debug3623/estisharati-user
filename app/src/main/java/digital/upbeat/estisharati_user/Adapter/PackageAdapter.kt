package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.CarouselHelper.CarouselLayoutManager
import digital.upbeat.estisharati_user.CarouselHelper.CarouselZoomPostLayoutListener
import digital.upbeat.estisharati_user.CarouselHelper.CenterScrollListener
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import digital.upbeat.estisharati_user.ViewHolder.OnlineCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.PackageViewHolder
import kotlinx.android.synthetic.main.activity_packages.*

class PackageAdapter(val context: Context, val packages: Packages, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<PackageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.package_item, parent, false)
        return PackageViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.show_exisiting_courses.setOnClickListener {
            context.startActivity(Intent(context, ExistingCourses::class.java))
        }
        holder.show_consultants_in_the_package.setOnClickListener {
            context.startActivity(Intent(context, ConsultantsInThePackage::class.java))
        }
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Finance and Accounting")
        arrayList.add("Health and Fitness")
        arrayList.add("Finance and Accounting")
        arrayList.add("Health and Fitness")
//        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false)
//        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
//        holder.consultant_profile_recycler.addOnScrollListener(CenterScrollListener())
        holder.consultant_profile_recycler.setHasFixedSize(true)
        holder.consultant_profile_recycler.removeAllViews()
        holder.consultant_profile_recycler.layoutManager = LinearLayoutManager(packages,LinearLayoutManager.HORIZONTAL,false)
        holder.consultant_profile_recycler.adapter = ConsultantProfileAdapter(context, packages, arrayList)
    }
}
