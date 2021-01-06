package digital.upbeat.estisharati_consultant.ApiHelper

import okhttp3.MultipartBody
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
    fun PROFILE_UPDATE_API_CALL(@Header("Authorization") token: String, @Field("fname") fname: String, @Field("lname") lname: String, @Field("phone") phone: String, @Field("phone_code") phone_code: String, @Field("email") email: String, @Field("qualification_brief") qualification_brief: String, @Field("country") country: String, @Field("city") city: String): Call<ResponseBody>

    @FormUrlEncoded @POST("notify")
    fun NOTIFY_API_CALL(@Header("Authorization") token: String, @Field("receiver_id") receiver_id: String, @Field("title") title: String, @Field("body") body: String, @Field("data") data: String): Call<ResponseBody>

    @Multipart @POST("upload-chatting-image")
    fun upload_chatting_image_API_CALL(@Header("Authorization") token: String, @Part profile_picture: MultipartBody.Part): Call<ResponseBody>


    @FormUrlEncoded @POST("user/change_password")
    fun CHANGE_PASSWORD_API_CALL(@Header("Authorization") token: String, @Field("old_password") old_password: String, @Field("password") password: String, @Field("password_confirmation") password_confirmation: String): Call<ResponseBody>

    @GET("pages/{pages}")
    fun PAGES_API_CALL(@Header("Authorization") token: String, @Path("pages") pages: String): Call<ResponseBody>

    @FormUrlEncoded @POST("contactus")
    fun CONTACTUS_API_CALL(@Header("Authorization") token: String, @Field("name") name: String, @Field("phone") phone: String, @Field("email") email: String, @Field("message") message: String): Call<ResponseBody>

     @GET("my-subsribers")
    fun MY_SUBSRIBERS_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @GET("notifications")
    fun NOTIFICATION_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

}
