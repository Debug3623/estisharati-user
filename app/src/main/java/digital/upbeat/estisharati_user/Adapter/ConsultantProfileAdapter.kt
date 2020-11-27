package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.Packages
import digital.upbeat.estisharati_user.ViewHolder.PackageViewHolder

class ConsultantProfileAdapter(val context: Context,val packages: Packages, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<PackageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.profile_logo_item, parent, false)
        return PackageViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {


    }
}
