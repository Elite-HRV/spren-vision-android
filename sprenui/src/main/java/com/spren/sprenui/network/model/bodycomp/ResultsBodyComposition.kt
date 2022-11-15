package com.spren.sprenui.network.model.bodycomp

data class ResultsBodyComposition(
    val guid: String,
    val bodyFat: Float,
    val leanMass: Float,
    val fatMass: Float,
    val androidFat: Float,
    val gynoidFat: Float,
    val androidByGynoid: Float,
    val metabolicRate: Float
)
