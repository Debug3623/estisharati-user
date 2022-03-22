package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Offers.Package
import digital.upbeat.estisharati_user.DataClassHelper.Packages.Data
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import digital.upbeat.estisharati_user.ViewHolder.PackageViewHolder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PackageAdapter(val context: Context, val packages: Packages?, val myPackages: MyPackages?, val offers: Offers?, val packagesArrayList: ArrayList<Data>, val offersPackagesArrayList: ArrayList<Package>) : RecyclerView.Adapter<PackageViewHolder>() {
    var helperMethods: HelperMethods


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.package_item, parent, false)
        return PackageViewHolder(layoutView)
    }

    init {
        helperMethods = HelperMethods(context)
    }

    override fun getItemCount(): Int {
        return if (offers != null) offersPackagesArrayList.size else packagesArrayList.size
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        lateinit var packagesItems :Data
        if (offers != null) {
             packagesItems = offersPackagesArrayList.get(position).subscription

            holder.OldPrice.text = packagesItems.price
            holder.offersEndDate.text = offersPackagesArrayList.get(position).enddate
            holder.packagePrice.text = offersPackagesArrayList.get(position).offerprice
            holder.offerLayout.visibility = View.VISIBLE
            holder.OldPrice.setPaintFlags( holder.OldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        } else if (packages != null) {
             packagesItems = packagesArrayList.get(position)
        if (packagesItems.offerprice.equals("0")) {
            holder.offerLayout.visibility = View.GONE
            holder.packagePrice.text = packagesItems.price
        } else {
            holder.offerLayout.visibility = View.VISIBLE
            holder.packagePrice.text = packagesItems.offerprice
            holder.OldPrice.text = packagesItems.price
            holder.offersEndDate.text = packagesItems.offer_end
            holder.OldPrice.setPaintFlags(holder.OldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        }
        } else if (myPackages != null) {
             packagesItems = packagesArrayList.get(position)
            holder.packagePrice.text = packagesItems.amount
            holder.offerLayout.visibility = View.GONE
        }

        holder.showExisitingCourses.setOnClickListener {
            val intent = Intent(context, ExistingCourses::class.java)
            intent.putExtra("courses", packagesItems.courses)
            context.startActivity(intent)
        }
        holder.showConsultantsInThePackage.setOnClickListener {
            val intent = Intent(context, ConsultantsInThePackage::class.java)
            intent.putExtra("consultants", packagesItems.consultants)
            context.startActivity(intent)
        }
        holder.packageName.text = packagesItems.name
        holder.packageDescription.text = packagesItems.description
        holder.packagePeriod.text = packagesItems.period
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
            holder.writtenHourse.text = context.getString(R.string.written_chat) + " " + packagesItems.features.written.time
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


        holder.consultantImage1.visibility = View.GONE
        holder.consultantImage2.visibility = View.GONE
        holder.consultantImage3.visibility = View.GONE
        holder.consultantImage4.visibility = View.GONE
        holder.consultantImage5.visibility = View.GONE

        for (consultantIndex in packagesItems.consultants.indices) {
            if (consultantIndex == 0) {
                holder.consultantImage1.visibility = View.VISIBLE
                Glide.with(context).load(packagesItems.consultants.get(consultantIndex).image_path).apply(helperMethods.requestOption).into(holder.consultantImage1)
            }
            if (consultantIndex == 1) {
                holder.consultantImage2.visibility = View.VISIBLE
                Glide.with(context).load(packagesItems.consultants.get(consultantIndex).image_path).apply(helperMethods.requestOption).into(holder.consultantImage2)
            }
            if (consultantIndex == 2) {
                holder.consultantImage3.visibility = View.VISIBLE
                Glide.with(context).load(packagesItems.consultants.get(consultantIndex).image_path).apply(helperMethods.requestOption).into(holder.consultantImage3)
            }
            if (consultantIndex == 3) {
                holder.consultantImage4.visibility = View.VISIBLE
                Glide.with(context).load(packagesItems.consultants.get(consultantIndex).image_path).apply(helperMethods.requestOption).into(holder.consultantImage4)
            }
            if (consultantIndex == 4) {
                holder.consultantImage5.visibility = View.VISIBLE
                Glide.with(context).load(packagesItems.consultants.get(consultantIndex).image_path).apply(helperMethods.requestOption).into(holder.consultantImage5)
            }
        }

        holder.packageLayout.setOnClickListener {
            offers?.let {
                offers.choosePackage(offersPackagesArrayList.get(position))
            }
        }
    }


}


