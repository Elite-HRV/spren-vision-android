package com.spren.sprenui.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Biomarkers(
    val hr: Score,
    val hrvScore: Score,
    val breathingRate: Score,
    val rmssd: Score
) : Parcelable
