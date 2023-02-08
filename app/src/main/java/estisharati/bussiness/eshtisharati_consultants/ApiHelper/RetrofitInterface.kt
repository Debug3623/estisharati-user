package estisharati.bussiness.eshtisharati_consultants.ApiHelper

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @FormUrlEncoded @POST("user/login")
    fun LOGIN_API_CALL(@Field("user_id") user_id: String, @Field("password") password: String, @Field("remember") remember: String, @Field("fire_base_token") fire_base_token: String, @Field("user_type") user_type : String): Call<ResponseBody>

    @GET("geographies")
    fun GEOGRAPHIES_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @Multipart @POST("user/profile/edit")
    fun FILE_UPDATE_API_CALL(@Header("Authorization") token: String, @Part file: MultipartBody.Part): Call<ResponseBody>

    @FormUrlEncoded @POST("user/profile/edit")
    fun UPDATE_FCM_API_CALL(@Header("Authorization") token: String, @Field("fire_base_token") fire_base_token: String): Call<ResponseBody>

    @FormUrlEncoded @POST("user/profile/edit")
    fun PROFILE_UPDATE_API_CALL(@Header("Authorization") token: String, @Field("fname") fname: String, @Field("lname") lname: String, @Field("phone") phone: String, @Field("phone_code") phone_code: String, @Field("email") email: String, @Field("qualification_brief") qualification_brief: String, @Field("country") country: String, @Field("city") city: String,@Field("user_type") user_type : String): Call<ResponseBody>

    @FormUrlEncoded @POST("notify")
    fun NOTIFY_API_CALL(@Header("Authorization") token: String, @Field("receiver_id") receiver_id: String, @Field("title") title: String, @Field("body") body: String,@Field("click_action") click_action: String, @Field("data") data: String): Call<ResponseBody>

    @Multipart @POST("upload-chatting-image")
    fun upload_chatting_image_API_CALL(@Header("Authorization") token: String, @Part profile_picture: MultipartBody.Part): Call<ResponseBody>

    @FormUrlEncoded @POST("user/change_password")
    fun CHANGE_PASSWORD_API_CALL(@Header("Authorization") token: String, @Field("old_password") old_password: String, @Field("password") password: String, @Field("password_confirmation") password_confirmation: String): Call<ResponseBody>

    @GET("pages/{pages}")
    fun PAGES_API_CALL(@Header("Authorization") token: String, @Path("pages") pages: String): Call<ResponseBody>

    @Multipart
    @POST("contactus")
    fun CONTACTUS_API_CALL(@Header("Authorization") token: String,  @Part("name")  name: RequestBody, @Part("phone") phone: RequestBody, @Part("email") email: RequestBody, @Part("message_type") message_type: RequestBody, @Part("subject") subject: RequestBody, @Part("message") message: RequestBody,@Part("user_id") user_id: RequestBody,@Part image: MultipartBody.Part?): Call<ResponseBody>

     @GET("my-subsribers")
    fun MY_SUBSRIBERS_API_CALL(@Header("Authorization") token: String,@Query("fire_base_token") fire_base_token: String): Call<ResponseBody>

    @GET("notifications")
    fun NOTIFICATION_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("clear-notifications")
    fun CLEAR_NOTIFICATIONS_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("consultant/comment")
    fun CONSULTANT_COMMENT_API_CALL(@Header("Authorization") token: String, @Field("consultant_id") consultant_id: String, @Field("parent_id") parent_id: String, @Field("comment") comment: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("courses/comment")
    fun COURSES_COMMENT_API_CALL(@Header("Authorization") token: String, @Field("course_id") course_id: String, @Field("parent_id") parent_id: String, @Field("comment") comment: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/reset_password")
    fun RESET_PASSWORD_API_CALL(@Field("phone") phone: String,@Field("user_type") user_type : String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/verify_reset_code")
    fun VERIFY_RESET_CODE_API_CALL(@Field("phone") phone: String, @Field("code") code: String, @Field("password") password: String): Call<ResponseBody>

    @GET("get_user_seconds/{user_id}")
    fun GET_USER_SECONDS_API_CALL(@Header("Authorization") token: String,@Path("user_id")user_id:String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("update-user-seconds")
    fun UPDATE_USER_SECONDS_API_CALL(@Header("Authorization") token: String, @Field("user_id") user_id: String, @Field("video_balance") video_balance: String, @Field("audio_balance") audio_balance: String, @Field("chat_minutes") chat_minutes: String,@Field("receiver_id") receiver_id:String,@Field("message") message:String,@Field("attachment") attachment:String): Call<ResponseBody>

    @GET("appointments")
    fun APPOINTMENTS_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/logout")
    fun LOGOUT_API_CALL(@Header("Authorization") token: String ,@Field("empty") empty: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("appove_reject")
    fun APPOVE_REJECT_API_CALL(@Header("Authorization") token: String ,@Field("appointment_id") appointment_id: String,@Field("status") status: String): Call<ResponseBody>

}