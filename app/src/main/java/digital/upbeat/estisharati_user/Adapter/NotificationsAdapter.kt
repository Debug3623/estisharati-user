package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.UI.Notifications
import digital.upbeat.estisharati_user.ViewHolder.NotificationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineCoursesViewHolder

class NotificationsAdapter(val context: Context, val notifications: Notifications, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<NotificationsViewHolder>() {
    val helperMethods: HelperMethods

    init {
        helperMethods = HelperMethods(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false)
        return NotificationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        val spanned = TextUtils.concat(helperMethods.getColorString("Successfully subscribed in", ContextCompat.getColor(context, R.color.dark_gray))," "+ helperMethods.getColorString("Introduction to social media Course", ContextCompat.getColor(context, R.color.black)))

        holder.message.text = spanned
        //        holder.message.text = helperMethods.getColorString("Successfully subscribed in", ContextCompat.getColor(context, R.color.dark_gray))
    }
}
