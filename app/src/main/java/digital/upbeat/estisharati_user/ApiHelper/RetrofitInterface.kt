package digital.upbeat.estisharati_user.ApiHelper

import digital.upbeat.estisharati_user.DataClassHelper.SendNotification.DataFcmBody
import digital.upbeat.estisharati_user.DataClassHelper.PaymentRequest.PaymentRequest
import digital.upbeat.estisharati_user.DataClassHelper.SubmitSurvey.SubmitSurvey
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @FormUrlEncoded
    @POST("user/login")
    fun LOGIN_API_CALL(@Field("user_id") user_id: String, @Field("password") password: String, @Field("remember") remember: String, @Field("fire_base_token") fire_base_token: String, @Field("user_type") user_type: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/register")
    fun REGISTER_API_CALL(@Field("fname") fname: String, @Field("lname") lname: String, @Field("email") email: String, @Field("phone") phone: String, @Field("phone_code") phone_code: String, @Field("password") password: String, @Field("user_type") user_type: String, @Field("referral_code") referral_code : String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/verify_phone")
    fun VERIFY_PHONE_API_CALL(@Field("phone") phone: String, @Field("code") code: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/resend_code")
    fun RESEND_CODE_API_CALL(@Field("phone") phone: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/reset_password")
    fun RESET_PASSWORD_API_CALL(@Field("phone") phone: String): Call<ResponseBody>

    @GET("geographies")
    fun GEOGRAPHIES_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/verify_reset_code")
    fun VERIFY_RESET_CODE_API_CALL(@Field("phone") phone: String, @Field("code") code: String, @Field("password") password: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("notify")
    fun NOTIFY_API_CALL(@Header("Authorization") token: String, @Field("receiver_id") receiver_id: String, @Field("title") title: String, @Field("body") body: String, @Field("data") data: String): Call<ResponseBody>

    @Multipart
    @POST("user/profile/edit")
    fun PROFILE_PICTURE_UPDATE_API_CALL(@Header("Authorization") token: String, @Part profile_picture: MultipartBody.Part): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/profile/edit")
    fun PROFILE_UPDATE_API_CALL(@Header("Authorization") token: String, @Field("fname") fname: String, @Field("lname") lname: String, @Field("email") email: String, @Field("country") country: String, @Field("city") city: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/profile/edit")
    fun UPDATE_FCM_API_CALL(@Header("Authorization") token: String, @Field("fire_base_token") fire_base_token: String): Call<ResponseBody>

    @Multipart
    @POST("upload-chatting-image")
    fun UPLOAD_CHATTING_IMAGE_API_CALL(@Header("Authorization") token: String, @Part profile_picture: MultipartBody.Part): Call<ResponseBody>

    @GET("courses")
    fun COURSES_API_CALL(@Header("Authorization") token: String, @Query("category_id") category_id: String, @Query("sortby") sortby: String, @Query("page") page: String): Call<ResponseBody>

    @GET("courses/{courseId}")
    fun COURSES_DETAILS_API_CALL(@Header("Authorization") token: String, @Path("courseId") courseId: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("favourites/favourite-course")
    fun FAVOURITE_COURSE_API_CALL(@Header("Authorization") token: String, @Field("course_id") course_id: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("favourites/favourite-consultant")
    fun FAVOURITE_CONSULTANT_API_CALL(@Header("Authorization") token: String, @Field("consultant_id") consultant_id: String): Call<ResponseBody>

    @GET("favourites")
    fun FAVOURITES_LIST_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("courses/comment")
    fun COURSES_COMMENT_API_CALL(@Header("Authorization") token: String, @Field("course_id") course_id: String, @Field("parent_id") parent_id: String, @Field("comment") comment: String): Call<ResponseBody>

    @GET("pages/{pages}")
    fun PAGES_API_CALL(@Header("Authorization") token: String, @Path("pages") pages: String): Call<ResponseBody>

    @GET("user/home")
    fun HOME_API_CALL(@Header("Authorization") token: String,@Query("fire_base_token") fire_base_token: String): Call<ResponseBody>

    @GET("consultants")
    fun ALL_CONSULTANTS_API_CALL(@Header("Authorization") token: String, @Query("category_id") category_id: String, @Query("sortby") sortby: String): Call<ResponseBody>

    @GET("consultant/{consultant_id}")
    fun CONSULTANT_API_CALL(@Header("Authorization") token: String, @Path("consultant_id") consultant_id: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("consultant/comment")
    fun CONSULTANT_COMMENT_API_CALL(@Header("Authorization") token: String, @Field("consultant_id") consultant_id: String, @Field("parent_id") parent_id: String, @Field("comment") comment: String): Call<ResponseBody>

    @GET("offers")
    fun OFFERS_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("faqs")
    fun FAQ_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("subscriptions")
    fun SUBSCRIPTIONS_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/change_password")
    fun CHANGE_PASSWORD_API_CALL(@Header("Authorization") token: String, @Field("old_password") old_password: String, @Field("password") password: String, @Field("password_confirmation") password_confirmation: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("contactus")
    fun CONTACTUS_API_CALL(@Header("Authorization") token: String, @Field("name") name: String, @Field("phone") phone: String, @Field("email") email: String, @Field("message_type") message_type: String, @Field("subject") subject: String, @Field("message") message: String): Call<ResponseBody>

    @GET("search")
    fun SEARCH_API_CALL(@Header("Authorization") token: String, @Query("search") search: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("courses/comment")
    fun MAIN_COURSES_COMMENT_API_CALL(@Header("Authorization") token: String, @Field("course_id") course_id: String, @Field("rate") rate: String, @Field("comment") comment: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("consultant/comment")
    fun MAIN_CONSULTANT_COMMENT_API_CALL(@Header("Authorization") token: String, @Field("consultant_id") consultant_id: String, @Field("category_id") category_id: String, @Field("rate") rate: String, @Field("comment") comment: String): Call<ResponseBody>

    @GET("notifications")
    fun NOTIFICATION_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("user/my-wallet")
    fun PAYMENTMETHOD_LIST_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("payment-methods")
    fun PAYMENTMETHOD_TYPE_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @POST("user/save-wallet")
    fun ADD_PAYMENT_API_CALL(@Header("Authorization") token: String, @Body paymentRequest: PaymentRequest): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/my-wallet/{payment_method_id}")
    fun DELETE_PAYMENTMETHOD__API_CALL(@Header("Authorization") token: String, @Path("payment_method_id") payment_method_id: String, @Field("payment_method_id") id: String): Call<ResponseBody>

    @GET("getcoupon")
    fun COUPON_API_CALL(@Header("Authorization") token: String, @Query("id") id: String, @Query("type") type: String, @Query("code") code: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user_subscription")
    fun USER_SUBSCRIPTION_API_CALL(@Header("Authorization") token: String, @Field("type") type: String, @Field("category_id") category_id  : String, @Field("chat") chat  : String,@Field("audio") audio  : String,@Field("video") video  : String, @Field("subscription_id") subscription_id: String, @Field("course_id") course_id: String, @Field("consultant_id") consultant_id: String,@Field("amount") amount: String, @Field("vat") vat: String, @Field("payment_method") payment_method: String, @Field("payment_reference_no") payment_reference_no: String, @Field("coupon_id") coupon_id: String, @Field("coupon_code") coupon_code: String, @Field("discount") discount: String,  @Field("referral_code") referral_code : String, @Field("referral_discount") referral_discount : String, @Field("referral_percent") referral_percent : String): Call<ResponseBody>


    @GET("my-consultants")
    fun MY_CONSULTANTS_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("my-courses")
    fun MY_COURSES_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("start-course/{course_id}")
    fun START_COURSE_API_CALL(@Header("Authorization") token: String,@Path("course_id")course_id:String): Call<ResponseBody>

    @GET("mypackages")
    fun MYPACKAGES_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("lesson-completed")
    fun LESSON_COMPLETED_API_CALL(@Header("Authorization") token: String, @Field("course_id") course_id: String, @Field("resource_id") resource_id: String, @Field("lesson_id") lesson_id: String): Call<ResponseBody>

    @GET("get-consultation-seconds/{consultant_id}")
    fun GET_CONSULTATION_SECONDS_API_CALL(@Header("Authorization") token: String,@Path("consultant_id")consultant_id:String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("update-consultation-seconds")
    fun UPDATE_CONSULTATION_SECONDS_API_CALL(@Header("Authorization") token: String, @Field("consultant_id") consultant_id: String, @Field("video_balance") video_balance: String, @Field("audio_balance") audio_balance: String, @Field("chat_minutes") chat_minutes: String,@Field("receiver_id") receiver_id:String,@Field("message") message:String,@Field("attachment") attachment:String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/logout")
    fun LOGOUT_API_CALL(@Header("Authorization") token: String,@Field("empty") empty: String): Call<ResponseBody>

    @GET("posts")
    fun POSTS_API_CALL(@Header("Authorization") token: String,@Query("sortby")sortby:String): Call<ResponseBody>

    @GET("surveys/{survey_id}")
    fun SURVEY_DETAILS_API_CALL(@Header("Authorization") token: String,@Path("survey_id")survey_id:String): Call<ResponseBody>

    @GET("surveys")
    fun SURVEYS_API_CALL(@Header("Authorization") token: String,@Query("sortby")sortby:String): Call<ResponseBody>

    @POST("surveys")
    fun SURVEYS_SUBMIT_API_CALL(@Header("Authorization") token: String,@Body() submitSurvey: SubmitSurvey): Call<ResponseBody>

    @GET("user/referral")
    fun REFERRAL_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/googlelogin")
    fun GOOGLE_LOGIN_API_CALL(@Field("google_id") google_id: String, @Field("firstname") firstname: String, @Field("lastname") lastname: String, @Field("email") email: String, @Field("image") image: String, @Field("fire_base_token") fire_base_token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("save_appointment")
    fun SAVE_APPOINTMENT_API_CALL(@Header("Authorization") token: String,@Field("consultant_id") consultant_id: String, @Field("date") firstname: String, @Field("time") lastname: String, @Field("category_id") category_id: String): Call<ResponseBody>

    @GET("appointments")
    fun APPOINTMENTS_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("categories")
    fun CATEGORIES_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("share_experience")
    fun POST_SHARE_EXPERIENCE_CONSULTANT_API_CALL(@Header("Authorization") token: String,@Field("consultant_id") consultant_id: String, @Field("category_id") category_id: String, @Field("experience") experience : String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("share_experience")
    fun POST_SHARE_EXPERIENCE_COURSE_API_CALL(@Header("Authorization") token: String, @Field("course_id") course_id : String, @Field("experience") experience : String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("share_experience_comments")
    fun SHARE_EXPERIENCE_COMMENTS_API_CALL(@Header("Authorization") token: String,@Field("experience_id") experience_id: String, @Field("comment") comment: String): Call<ResponseBody>

    @GET("share_experience")
    fun GET_SHARE_EXPERIENCE_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("share_experience/{id}")
    fun GET_SHARE_EXPERIENCE_BY_ID_API_CALL(@Header("Authorization") token: String,@Path("id")id:String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("userpost")
    fun PUPLISH_POST_API_CALL(@Header("Authorization") token: String, @Field("content") content: String): Call<ResponseBody>

    @GET("userpost/")
    fun GET_POSTS_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("userpost/{id}")
    fun GET_POST_BY_ID_API_CALL(@Header("Authorization") token: String,@Path("id")id:String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("userpost/comment")
    fun SHARE_POST_COMMENTS_API_CALL(@Header("Authorization") token: String,@Field("post_id") post_id: String, @Field("comment") comment: String): Call<ResponseBody>

}
