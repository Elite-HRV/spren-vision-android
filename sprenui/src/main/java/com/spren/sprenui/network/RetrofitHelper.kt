package com.spren.sprenui.network

import com.spren.sprenui.SprenUI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RetrofitHelper {

    fun getInstance(): Retrofit {
        val client = OkHttpClient.Builder().addInterceptor(HeaderInterceptor())
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS).build()
        return Retrofit.Builder().baseUrl(SprenUI.Config.baseURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}