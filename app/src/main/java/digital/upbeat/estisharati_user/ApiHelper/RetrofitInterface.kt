package digital.upbeat.estisharati_user.ApiHelper

import digital.upbeat.estisharati_user.DataClassHelper.DataFcmBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @FormUrlEncoded
    @POST("user/login")
    fun LOGIN_API_CALL(@Field("user_id") user_id: String, @Field("password") password: String,@Field("remember") remember:String,@Field("fire_base_token") fire_base_token:String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/test_register")
    fun REGISTER_API_CALL(@Field("fname") fname: String, @Field("lname") lname: String, @Field("email") email: String, @Field("phone") phone: String, @Field("password") password: String, @Field("user_type") user_type: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/verify_phone")
    fun VERIFY_PHONE_API_CALL(@Field("phone") phone: String, @Field("code") code: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/test_resend_code")
    fun RESEND_CODE_API_CALL(@Field("phone") phone: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/test_reset_password")
    fun RESET_PASSWORD_API_CALL(@Field("phone") phone: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/verify_reset_code")
    fun VERIFY_RESET_CODE_API_CALL(@Field("phone") phone: String, @Field("code") code: String, @Field("password") password: String): Call<ResponseBody>

    @Headers("Authorization: key=AAAALTvNfY4:APA91bFOPQ7yoHyA85qfqOslNzhlOll9prOzyW3LUi9x3KofyRI0YeHlEv_0hv8OrI3BnJaEytF-d6sq48y7NxRFQabeOYcwGYqDMvhI2gw5Mjz0lSNRWvSDs4nRE3jdHujTvFfIX1wv")
    @POST("fcm/send")
    fun FCM_SEND_API_CALL(@Body dataFcmBody: DataFcmBody): Call<ResponseBody>

    @Multipart
    @POST("user/profile/edit")
    fun PROFILE_PICTURE_UPDATE_API_CALL(@Header("Authorization") token:String,@Part profile_picture: MultipartBody.Part): Call<ResponseBody>



}
