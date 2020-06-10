package com.hexagonitsolutions.ridehomeuser.ApiHelper


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {


    @GET("Auth/authentication")
    fun AUTHENTICATION_API_CALL(): Call<ResponseBody>


    @FormUrlEncoded
    @POST("user/login")
    fun LOGIN_API_CALL(@Field("user_id") user_id: String, @Field("password") password: String): Call<ResponseBody>




}
