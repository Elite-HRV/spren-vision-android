package com.spren.sprenui.network.model

data class ResultsFingerCamera(
    val guid: String,
    val hr: Float,
    val hrvScore: Float,
    val rmssd: Float,
    val breathingRate: Float,
    val readiness: Float?,
    val ansBalance: Float?,
    val signalQuality: Float
)
