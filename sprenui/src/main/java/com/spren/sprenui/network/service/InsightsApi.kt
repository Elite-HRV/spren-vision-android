package com.spren.sprenui.network.service

import com.spren.sprenui.network.model.Insights
import com.spren.sprenui.network.model.ResultId
import com.spren.sprenui.network.model.Results
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InsightsApi {
    @POST("/submit/sdkData")
    suspend fun getInsights(@Body insights: Insights): Response<ResultId>

    @GET("/results/{guid}")
    suspend fun getResults(@Path("guid") guid: String): Response<Results>
}