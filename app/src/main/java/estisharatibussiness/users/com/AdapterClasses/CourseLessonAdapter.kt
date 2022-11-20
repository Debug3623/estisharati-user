package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.DataClassHelperMehtods.StartCourse.Lesson
import estisharatibussiness.users.com.FragmentClasses.CourseDocuments
import estisharatibussiness.users.com.FragmentClasses.CourseVideos
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.PhotoViewer
import estisharatibussiness.users.com.UserInterfaces.WebViewActivity
import estisharatibussiness.users.com.PublicViewHolder.*

class CourseLessonAdapter(val context: Context, val courseVideos: CourseVideos?, val courseDocuments: CourseDocuments?, val lessons: ArrayList<Lesson>) : RecyclerView.Adapter<CourseLessonVideoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseLessonVideoViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.course_lesson_item, parent, false)
        return CourseLessonVideoViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: CourseLessonVideoViewHolder, position: Int) {
        holder.lessonsTitle.text = lessons.get(position).title

        if (courseVideos != null) {
            if (courseVideos.activityCourseResource.startCourseResponse.data.downloadable) {
                holder.lessonsDownload.visibility = View.VISIBLE
            } else {
                holder.lessonsDownload.visibility = View.GONE
            }
            if (lessons.get(position).watched) {
                holder.lessonsTitle.setTextColor(ContextCompat.getColorStateList(context, R.color.orange))
            } else {
                holder.lessonsTitle.setTextColor(ContextCompat.getColorStateList(context, R.color.black_light))
            }
            if (GlobalData.lessonsPlayingPosition == lessons.get(position).position) {
                holder.lessonsPlaying.visibility = View.VISIBLE
                holder.lessonsTitle.setTextColor(ContextCompat.getColorStateList(context, R.color.orange))
            } else {
                holder.lessonsPlaying.visibility = View.GONE
            }
        } else if (courseDocuments != null) {
            if (courseDocuments.activityCourseResource.startCourseResponse.data.downloadable) {
                holder.lessonsDownload.visibility = View.VISIBLE
            } else {
                holder.lessonsDownload.visibility = View.GONE
            }
        }



        holder.lessonsLayout.setOnClickListener {
            if (courseVideos != null) {
                courseVideos.activityCourseResource.changePlayerPosition(lessons.get(position).position)
            } else if (courseDocuments != null) {
                val fileType = lessons.get(position).type.split("/")
                if (fileType.get(0).equals("image")) {
                    val intent = Intent(context, PhotoViewer::class.java)
                    intent.putExtra("name", lessons.get(position).title)
                    intent.putExtra("image_url", lessons.get(position).lesson_file)
                    intent.putExtra("send_time", "")
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, WebViewActivity::class.java)
                    intent.putExtra("name", lessons.get(position).title)
                    intent.putExtra("url", lessons.get(position).lesson_file)
                    Log.d("titleUrl",lessons.get(position).lesson_file)
                    context.startActivity(intent)
                }
            }
        }
        holder.lessonsDownload.setOnClickListener {
            holder.lessonsDownload.setImageResource(R.drawable.ic_download_arrow_green)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lessons.get(position).lesson_file))
            context.startActivity(intent)
        }
    }
}
