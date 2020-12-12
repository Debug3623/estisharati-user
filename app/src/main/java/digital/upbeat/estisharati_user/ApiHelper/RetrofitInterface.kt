package digital.upbeat.estisharati_user.ApiHelper

import digital.upbeat.estisharati_user.DataClassHelper.DataFcmBody
import digital.upbeat.estisharati_user.DataClassHelper.PaymentRequest.PaymentRequest
import digital.upbeat.estisharati_user.DataClassHelper.data
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @FormUrlEncoded
    @POST("user/login")
    fun LOGIN_API_CALL(@Field("user_id") user_id: String, @Field("password") password: String, @Field("remember") remember: String, @Field("fire_base_token") fire_base_token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/test_register")
    fun REGISTER_API_CALL(@Field("fname") fname: String, @Field("lname") lname: String, @Field("email") email: String, @Field("phone") phone: String, @Field("phone_code") phone_code: String, @Field("password") password: String, @Field("user_type") user_type: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/verify_phone")
    fun VERIFY_PHONE_API_CALL(@Field("phone") phone: String, @Field("code") code: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/test_resend_code")
    fun RESEND_CODE_API_CALL(@Field("phone") phone: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/test_reset_password")
    fun RESET_PASSWORD_API_CALL(@Field("phone") phone: String): Call<ResponseBody>

    @GET("geographies")
    fun GEOGRAPHIES_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/verify_reset_code")
    fun VERIFY_RESET_CODE_API_CALL(@Field("phone") phone: String, @Field("code") code: String, @Field("password") password: String): Call<ResponseBody>

    @Headers("Authorization: key=AAAALTvNfY4:APA91bFOPQ7yoHyA85qfqOslNzhlOll9prOzyW3LUi9x3KofyRI0YeHlEv_0hv8OrI3BnJaEytF-d6sq48y7NxRFQabeOYcwGYqDMvhI2gw5Mjz0lSNRWvSDs4nRE3jdHujTvFfIX1wv")
    @POST("fcm/send")
    fun FCM_SEND_API_CALL(@Body dataFcmBody: DataFcmBody): Call<ResponseBody>

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
    fun upload_chatting_image_API_CALL(@Header("Authorization") token: String, @Part profile_picture: MultipartBody.Part): Call<ResponseBody>

    @GET("courses")
    fun COURSES_API_CALL(@Header("Authorization") token: String, @Query("category_id") category_id: String, @Query("sortby") sortby: String): Call<ResponseBody>

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
    fun HOME_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

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
    fun CONTACTUS_API_CALL(@Header("Authorization") token: String, @Field("name") name: String, @Field("phone") phone: String, @Field("email") email: String, @Field("message") message: String): Call<ResponseBody>

    @GET("search")
    fun SEARCH_API_CALL(@Header("Authorization") token: String, @Query("search") search: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("courses/comment")
    fun MAIN_COURSES_COMMENT_API_CALL(@Header("Authorization") token: String, @Field("course_id") course_id: String, @Field("rate") rate: String, @Field("comment") comment: String): Call<ResponseBody>

    @GET("notifications")
    fun NOTIFICATION_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("user/my-wallet")
    fun PAYMENTMETHOD_LIST_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("payment-methods")
    fun PAYMENTMETHOD_TYPE_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @POST("user/save-wallet")
    fun ADD_PAYMENT_API_CALL(@Header("Authorization") token: String, @Body paymentRequest: PaymentRequest): Call<ResponseBody>
}
