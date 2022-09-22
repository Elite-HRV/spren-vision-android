package com.spren.sprenui.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.Settings

object UserId {
    @SuppressLint("HardwareIds")
    fun get(contentResolver: ContentResolver): String = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ANDROID_ID
    )
}