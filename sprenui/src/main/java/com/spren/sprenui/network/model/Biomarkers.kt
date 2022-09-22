package com.spren.sprenui.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Biomarkers(val hr: Score, val hrvScore: Score) : Parcelable
