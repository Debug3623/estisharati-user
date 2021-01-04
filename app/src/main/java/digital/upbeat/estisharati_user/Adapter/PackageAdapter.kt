package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Packages.Data
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import digital.upbeat.estisharati_user.ViewHolder.PackageViewHolder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PackageAdapter(val context: Context, val packages: Packages?, val myPackages: MyPackages?, val packagesArrayList: ArrayList<Data>) : RecyclerView.Adapter<PackageViewHolder>() {
    lateinit var helperMethods: HelperMethods
    var purchased = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.package_item, parent, false)
        return PackageViewHolder(layoutView)
    }

    init {
        helperMethods = HelperMethods(context)
    }

    override fun getItemCount(): Int {
        return packagesArrayList.size
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        if (packages != null) {
            purchased = false
        } else if (myPackages != null) {
            purchased = true
        }
        val packagesItems = packagesArrayList.get(position)
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
        holder.packagePrice.text = packagesItems.price
        holder.packagePeriod.text = packagesItems.period
        if (packagesItems.features.video.time.equals("0")) {
            holder.videoLayout.visibility = View.GONE
        } else {
            holder.videoLayout.visibility = View.VISIBLE
            holder.videoHourse.text = "Video Call " + formatToMinute(packagesItems.features.video.time)
        }
        if (packagesItems.features.audio.time.equals("0")) {
            holder.voiceLayout.visibility = View.GONE
        } else {
            holder.voiceLayout.visibility = View.VISIBLE
            holder.voiceHourse.text = "Voice Call " + formatToMinute(packagesItems.features.audio.time)
        }
        if (packagesItems.features.written.time.equals("0")) {
            holder.writtenLayout.visibility = View.GONE
        } else {
            holder.writtenLayout.visibility = View.VISIBLE
            holder.writtenHourse.text = "Written Chat " + formatToMinute(packagesItems.features.written.time)
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
    }

    fun formatToMinute(minute: String): String {
        var sdf = SimpleDateFormat("mm", Locale.US)

        try {
            val dt: Date = sdf.parse(minute)
            sdf = SimpleDateFormat("HH:mm", Locale.US)
            return sdf.format(dt)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }
}


