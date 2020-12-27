package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.StartCourse.Document
import digital.upbeat.estisharati_user.DataClassHelper.StartCourse.Video
import digital.upbeat.estisharati_user.Fragment.CourseDocuments
import digital.upbeat.estisharati_user.Fragment.CourseVideos
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.ViewHolder.*

class CourseChapterAdapter(val context: Context, val courseVideos: CourseVideos?, val courseDocuments: CourseDocuments?, val videos: ArrayList<Video>, val documents: ArrayList<Document>) : RecyclerView.Adapter<CourseChapterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseChapterViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.course_chapter_item, parent, false)
        return CourseChapterViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return when {
            courseVideos != null -> {
                videos.size
            }
            courseDocuments != null -> {
                documents.size
            }
            else -> {
                0
            }
        }
    }

    override fun onBindViewHolder(holder: CourseChapterViewHolder, position: Int) {
        holder.courseLessonRecycler.setHasFixedSize(true)
        holder.courseLessonRecycler.removeAllViews()
        holder.courseLessonRecycler.layoutManager = LinearLayoutManager(context)
        if (courseVideos != null) {
            holder.chapterTitle.text = videos.get(position).chapter_title
            holder.courseLessonRecycler.adapter = CourseLessonAdapter(context, courseVideos, courseDocuments, videos.get(position).lessons)
        } else if (courseDocuments != null) {
            holder.chapterTitle.text = documents.get(position).chapter_title
            holder.courseLessonRecycler.adapter = CourseLessonAdapter(context, courseVideos, courseDocuments, documents.get(position).lessons)
        }

    }
}
