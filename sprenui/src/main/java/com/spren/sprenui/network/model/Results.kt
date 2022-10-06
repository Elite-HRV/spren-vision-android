package com.spren.sprenui.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Results(val biomarkers: Biomarkers, val insights: Readiness, val signalQuality: Score) :
    Parcelable