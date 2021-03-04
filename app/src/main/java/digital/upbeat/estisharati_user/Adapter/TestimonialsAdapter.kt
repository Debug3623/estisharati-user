package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Testimonials.Data
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.Testimonials
import digital.upbeat.estisharati_user.UI.TestimonialsDetails
import digital.upbeat.estisharati_user.ViewHolder.TestimonialsViewHolder

class TestimonialsAdapter(val context: Context, val testimonials: Testimonials, var testimonialsArrayList: ArrayList<Data>) : RecyclerView.Adapter<TestimonialsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestimonialsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.testimonials_item, parent, false)
        return TestimonialsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return testimonialsArrayList.size
    }

    override fun onBindViewHolder(holder: TestimonialsViewHolder, position: Int) {
        Glide.with(context).load(testimonialsArrayList.get(position).user.image_path).apply(testimonials.helperMethods.requestOption).into(holder.profilePicture)
        holder.userName.text = testimonialsArrayList.get(position).user.name
        holder.commentsCount.text = testimonialsArrayList.get(position).comments_count + " " + context.getString(R.string.comments)
        if (testimonialsArrayList.get(position).type.equals("consultant")) {
            holder.testimonialType.text = testimonialsArrayList.get(position).service_name + " ( " + testimonialsArrayList.get(position).consultant_category + " )"
        } else {
            holder.testimonialType.text = testimonialsArrayList.get(position).service_name
        }
        holder.testimonialsContent.text = testimonialsArrayList.get(position).experience

        holder.holeLayout.setOnClickListener {
         val intent=   Intent(context, TestimonialsDetails::class.java)
            intent.putExtra("testimonialId",testimonialsArrayList.get(position).id)
            context.startActivity(intent)
        }
    }
}
