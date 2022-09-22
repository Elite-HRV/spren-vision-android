package com.spren.sprenui.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Score(val value: Double?, val status: String, val errorDescription: String?): Parcelable
