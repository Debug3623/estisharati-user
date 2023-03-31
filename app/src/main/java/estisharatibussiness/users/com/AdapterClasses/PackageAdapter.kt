package estisharatibussiness.users.com.AdapterClasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.DataClassHelperMehtods.Offers.Package
import estisharatibussiness.users.com.DataClassHelperMehtods.Packages.Data
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.*
import estisharatibussiness.users.com.PublicViewHolder.PackageViewHolder
import kotlin.collections.ArrayList

class PackageAdapter(val context: Context, val activityPackages: ActivityPackages?, val activityMyPackages: ActivityMyPackages?, val activityOffers: ActivityOffers?, val packagesArrayList: ArrayList<Data>, val offersPackagesArrayList: ArrayList<Package>) : RecyclerView.Adapter<PackageViewHolder>() {
    var helperMethods: HelperMethods


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.package_item, parent, false)
        return PackageViewHolder(layoutView)
    }

    init {
        helperMethods = HelperMethods(context)
    }

    override fun getItemCount(): Int {
        return if (activityOffers != null) offersPackagesArrayList.size else packagesArrayList.size
    }

    @SuppressLint("SetTextI18n") override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
       lateinit var packagesItems :Data
        if (activityOffers != null) {
             packagesItems = offersPackagesArrayList.get(position).subscription
            holder.OldPrice.text = packagesItems.price
            holder.offersEndDate.text = offersPackagesArrayList.get(position).enddate
            holder.packagePrice.text = offersPackagesArrayList.get(position).offerprice
            holder.offerLayout.visibility = View.VISIBLE
            holder.OldPrice.paintFlags = holder.OldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        } else if (activityPackages != null) {
             packagesItems = packagesArrayList.get(position)
        if (packagesItems.offerprice.equals("0")) {
            holder.offerLayout.visibility = View.GONE
            holder.packagePrice.text = packagesItems.price
        } else {
            holder.offerLayout.visibility = View.VISIBLE
            holder.packagePrice.text = packagesItems.offerprice
            holder.OldPrice.text = packagesItems.price
            holder.offersEndDate.text = packagesItems.offer_end
            holder.OldPrice.paintFlags = holder.OldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        } else if (activityMyPackages != null) {
             packagesItems = packagesArrayList.get(position)
            holder.packagePrice.text = packagesItems.amount
            holder.offerLayout.visibility = View.GONE
        }

        holder.showExisitingCourses.setOnClickListener {
            val intent = Intent(context, ExistingCourses::class.java)
            intent.putExtra("courses", packagesItems.courses)
            context.startActivity(intent)
        }

        holder.packageName.text = packagesItems.name

        if(packagesItems.description==""){
            holder.packageDescription.text = "Sorry No Description is available for these package"
        }else{
            holder.packageDescription.text = packagesItems.description
        }
        holder.packagePeriod.text = packagesItems.period

        Log.d("description++",holder.packageDescription.text.toString())



        if (packagesItems.features.video.time.equals("0")) {
            holder.videoLayout.visibility = View.GONE
        } else {
            holder.videoLayout.visibility = View.VISIBLE
            holder.videoHourse.text = context.getString(R.string.video_call) + " " + helperMethods.formatToMinute(packagesItems.features.video.time)
        }
        if (packagesItems.features.audio.time.equals("0")) {
            holder.voiceLayout.visibility = View.GONE
        } else {
            holder.voiceLayout.visibility = View.VISIBLE
            holder.voiceHourse.text = context.getString(R.string.voice_call) + " " + helperMethods.formatToMinute(packagesItems.features.audio.time)
        }
        if (packagesItems.features.written.time.equals("0")) {
            holder.writtenLayout.visibility = View.GONE
        } else {
            holder.writtenLayout.visibility = View.VISIBLE
             if(packagesItems.price.toInt() >= 600){
                 holder.writtenHourse.text = context.getString(R.string.written_chat_package) + " " + "16:00:00"

             }else if(packagesItems.price.toInt() <= 50){
                 holder.writtenHourse.text = context.getString(R.string.written_chat) + " " + "200 (1 message)"
            }

             else{
                 holder.writtenHourse.text =context.getString(R.string.meeting2)
             }
//            holder.writtenHourse.text = context.getString(R.string.written_chat) + " " + packagesItems.features.written.time
        }
        if (packagesItems.courses.size > 0) {
            holder.courseLayout.visibility = View.VISIBLE
        } else {
            holder.courseLayout.visibility = View.GONE
        }
        if (packagesItems.consultants.size > 0) {
            holder.consultantLayout.visibility = View.VISIBLE
        } else {
            holder.consultantLayout.visibility = View.GONE
        }


        holder.packageLayout.setOnClickListener {

            activityOffers?.let {
                activityOffers.choosePackage(offersPackagesArrayList.get(position))
            }
        }
    }


}


