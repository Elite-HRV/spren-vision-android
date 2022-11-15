package com.spren.sprenui.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context

object HardwareCheck {
    fun isHighPerformingDevice(activity: Activity): Boolean {
        val activityManager =
            activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return !activityManager.isLowRamDevice && Runtime.getRuntime()
            .availableProcessors() >= 8 && activityManager.memoryClass >= 192
    }
}