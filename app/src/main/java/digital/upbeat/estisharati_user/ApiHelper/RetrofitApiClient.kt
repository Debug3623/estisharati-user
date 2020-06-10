package com.hexagonitsolutions.ridehomeuser.ApiHelper

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitApiClient(private val Base_url: String) {
    private lateinit var retrofit: Retrofit

    fun getRetrofit(): Retrofit {

        val client = OkHttpClient.Builder()
        client.connectTimeout(2, TimeUnit.MINUTES)
        client.readTimeout(2, TimeUnit.MINUTES)
        client.writeTimeout(2, TimeUnit.MINUTES)

        retrofit = Retrofit.Builder().baseUrl(Base_url)
            .addConverterFactory(GsonConverterFactory.create()).client(client.build()).build()

        return retrofit
    }
}
