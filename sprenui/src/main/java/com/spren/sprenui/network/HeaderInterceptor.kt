package com.spren.sprenui.network

import com.spren.sprenui.SprenUI
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("X-API-KEY", SprenUI.Config.apiKey)
                .build()
        )
    }
}