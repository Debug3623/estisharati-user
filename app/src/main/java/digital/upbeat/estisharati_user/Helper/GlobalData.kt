package digital.upbeat.estisharati_user.Helper

import com.google.android.exoplayer2.MediaItem
import digital.upbeat.estisharati_user.DataClassHelper.Chat.DataUserFireStore
import digital.upbeat.estisharati_user.DataClassHelper.Home.HomeResponse
import digital.upbeat.estisharati_user.DataClassHelper.PackagesOptions.PackagesOptions
import digital.upbeat.estisharati_user.DataClassHelper.StartCourse.Lesson
import digital.upbeat.estisharati_user.DataClassHelper.Testimonials.TestimonialsResponse
import digital.upbeat.estisharati_user.DataClassHelper.posts.PostsResponse

object GlobalData {
    const val PICK_IMAGE_GALLERY = 1
    const val PICK_IMAGE_CAMERA = 2
    var BaseUrl = "https://super-servers.com/estisharati/api/v1/en/"
    var profileUpdate = false
    var FcmToken = ""
    var forwardType = ""
    var forwardContent = ""
    var referralCode = ""
    var courseId = ""
    lateinit var homeResponse: HomeResponse
     var homeResponseMain: HomeResponse?=null
    fun isThingInitialized() = ::homeResponse.isInitialized
    var packagesOptions = PackagesOptions("", "", "", "", "", "", "", "", "0", "", "", "", "", "", "", "", "")
    var FullScreen = false
    val mediaItemArrayList: ArrayList<MediaItem> = arrayListOf()
    val lessonArrayList: ArrayList<Lesson> = arrayListOf()
    var lessonsPlayingPosition = 0
    var lessonsPlayingDuration: Long = 0
    var allUserArraylist = arrayListOf<DataUserFireStore>()

     var testimonialsResponse: TestimonialsResponse?=null
     var postsResponse: PostsResponse?=null

}