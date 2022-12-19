package estisharatibussiness.users.com.Helper

import com.google.android.exoplayer2.MediaItem
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataUserFireStore
import estisharatibussiness.users.com.DataClassHelperMehtods.Home.HomeResponse
import estisharatibussiness.users.com.DataClassHelperMehtods.PackagesOptions.PackagesOptions
import estisharatibussiness.users.com.DataClassHelperMehtods.StartCourse.Lesson
import estisharatibussiness.users.com.DataClassHelperMehtods.Testimonials.TestimonialsResponse
import estisharatibussiness.users.com.DataClassHelperMehtods.posts.PostsResponse

object GlobalData {
    const val PICK_IMAGE_GALLERY = 1
    const val PICK_IMAGE_CAMERA = 2
//    var BaseUrl = "https://super-servers.com/estisharati/api/v1/en/"
    var BaseUrl = "https://apptocom.com/estisharati/api/v1/en/"
    var profileUpdate = false
    var FcmToken = ""
    var forwardType = ""
    var forwardContent = ""
    var referralCode = ""
    var courseId = ""
    var surveyId = ""
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