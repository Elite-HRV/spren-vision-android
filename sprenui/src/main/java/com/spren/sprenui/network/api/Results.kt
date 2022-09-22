package com.spren.sprenui.network.api

import com.spren.sprenui.SprenUI
import com.spren.sprenui.network.model.Insights
import com.spren.sprenui.network.model.Results
import com.spren.sprenui.network.service.InsightsApi
import com.spren.sprenui.util.ReadingStatus
import kotlinx.coroutines.*

suspend fun getResults(
    insightsApi: InsightsApi,
    readingData: String
): Results? = coroutineScope {
    val resultId = insightsApi.getInsights(Insights(SprenUI.Config.userId, readingData))
    val guid = resultId.body()?.guid!!
    SprenUI.LatestRequest.guid = guid
    var results: Results?
    coroutineScope {
        do {
            results = insightsApi.getResults(guid).body()
            delay(1000)
        } while (results?.biomarkers?.hr?.status == ReadingStatus.PENDING.value || results?.biomarkers?.hrvScore?.status == ReadingStatus.PENDING.value)
    }
    return@coroutineScope results
}