package estisharati.bussiness.eshtisharati_consultants.Helper

import estisharati.bussiness.eshtisharati_consultants.DataClassHelper.MySubsribers.MySubscriberResponse
import estisharati.bussiness.eshtisharati_consultants.DataClassHelper.Notification.NotificationsResponse

object GlobalData {
    const val PICK_IMAGE_GALLERY = 1
    const val PICK_IMAGE_CAMERA = 2
    var BaseUrl = "https://apptocom.com/estisharati/api/v1/en/"
    var profileUpdate=false
    var FcmToken=""
    const val DOC = "application/msword"
    const val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    const val IMAGE = "image/*"
    const val AUDIO = "audio/*"
    const val TEXT = "text/*"
    const val PDF = "application/pdf"
    const val CSV = "application/csv"
    const val XLS = "application/vnd.ms-excel"

    var forwardType = ""
    var forwardContent = ""
    lateinit var mySubscriberResponse: MySubscriberResponse
    fun isInitialized() = ::mySubscriberResponse.isInitialized
    var notificationResponse: NotificationsResponse = NotificationsResponse("","", arrayListOf())

}