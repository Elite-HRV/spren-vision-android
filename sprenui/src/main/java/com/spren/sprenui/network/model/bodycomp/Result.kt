package com.spren.sprenui.network.model.bodycomp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Result(
    val bodyFat: BodyFat,
    val leanMass: LeanMass,
    val fatMass: FatMass,
    val androidFat: AndroidFat,
    val gynoidFat: GynoidFat,
    val androidByGynoid: AndroidByGynoidFat,
    val metabolicRate: MetabolicRate,
) : Parcelable