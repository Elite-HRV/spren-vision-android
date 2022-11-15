package com.spren.sprenui.network.model.bodycomp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MetabolicRate(val value: Double, val status: String, val errorDescription: String): Parcelable
