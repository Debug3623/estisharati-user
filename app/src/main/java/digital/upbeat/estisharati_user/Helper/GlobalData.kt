package digital.upbeat.estisharati_user.Helper

import digital.upbeat.estisharati_user.DataClassHelper.Home.HomeResponse
import digital.upbeat.estisharati_user.DataClassHelper.PackagesOptions.PackagesOptions
import org.intellij.lang.annotations.Language

object GlobalData {
    const val PICK_IMAGE_GALLERY = 1
    const val PICK_IMAGE_CAMERA = 2
    var LanguageCode = "en"
    val BaseUrl = "https://super-servers.com/estisharati/api/v1/$LanguageCode/"
    var profileUpdate = false
    var FcmToken = ""
    var forwardType = ""
    var forwardContent = ""
    lateinit var homeResponse: HomeResponse
    fun isThingInitialized() = ::homeResponse.isInitialized
    var packagesOptions = PackagesOptions("", "","", "", "", "", "")
}