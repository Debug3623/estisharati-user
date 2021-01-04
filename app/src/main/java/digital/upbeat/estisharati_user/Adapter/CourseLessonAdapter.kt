package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.StartCourse.Lesson
import digital.upbeat.estisharati_user.Fragment.CourseDocuments
import digital.upbeat.estisharati_user.Fragment.CourseVideos
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.PhotoViewer
import digital.upbeat.estisharati_user.UI.WebView
import digital.upbeat.estisharati_user.ViewHolder.*

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
            if (courseVideos.courseResource.startCourseResponse.data.downloadable) {
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
            if (courseDocuments.courseResource.startCourseResponse.data.downloadable) {
                holder.lessonsDownload.visibility = View.VISIBLE
            } else {
                holder.lessonsDownload.visibility = View.GONE
            }
        }



        holder.lessonsLayout.setOnClickListener {
            if (courseVideos != null) {
                courseVideos.courseResource.changePlayerPosition(lessons.get(position).position)
            } else if (courseDocuments != null) {
                val fileType = lessons.get(position).type.split("/")
                if (fileType.get(0).equals("image")) {
                    val intent = Intent(context, PhotoViewer::class.java)
                    intent.putExtra("name", lessons.get(position).title)
                    intent.putExtra("image_url", lessons.get(position).lesson_file)
                    intent.putExtra("send_time", "")
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, WebView::class.java)
                    intent.putExtra("name", lessons.get(position).title)
                    intent.putExtra("url", lessons.get(position).lesson_file)
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
