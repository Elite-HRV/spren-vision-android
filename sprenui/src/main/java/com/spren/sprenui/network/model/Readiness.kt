package com.spren.sprenui.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Readiness(val readiness: Score): Parcelable
