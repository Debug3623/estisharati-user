package digital.upbeat.estisharati_user.ApiHelper


import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @FormUrlEncoded
    @POST("user/login")
    fun LOGIN_API_CALL(@Field("user_id") user_id: String, @Field("password") password: String, @Field("remember") remember: String, @Field("fire_base_token") fire_base_token: String): Call<ResponseBody>

    @GET("geographies")
    fun GEOGRAPHIES_API_CALL(@Header("Authorization") token: String): Call<ResponseBody>

    @Multipart
    @POST("user/profile/edit")
    fun FILE_UPDATE_API_CALL(@Header("Authorization") token: String, @Part file: MultipartBody.Part): Call<ResponseBody>

    @FormUrlEncoded
    @POST("user/profile/edit")
    fun UPDATE_FCM_API_CALL(@Header("Authorization") token: String, @Field("fire_base_token") fire_base_token: String): Call<ResponseBody>

}
