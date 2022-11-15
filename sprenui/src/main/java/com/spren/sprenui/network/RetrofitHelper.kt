package com.spren.sprenui.network

import com.spren.sprenui.SprenUI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitHelper {

    fun getInstance(): Retrofit {
        val logging = HttpLoggingInterceptor()

        // TODO: Set NONE for non-dev
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(logging)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS).build()

        return Retrofit.Builder().baseUrl(SprenUI.Config.baseURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}