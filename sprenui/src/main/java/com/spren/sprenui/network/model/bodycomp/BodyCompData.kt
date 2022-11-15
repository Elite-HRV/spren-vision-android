package com.spren.sprenui.network.model.bodycomp

data class BodyCompData(
    val timeInfo: TimeInfo,
    val gender: String,
    val age: Int,
    val weight: Double,
    val height: Double,
    val vigorousDays: Int,
    val pushUps: Int,
    val image: String
)
