package com.spren.sprenui.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object Permissions {
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    fun allPermissionsGranted(baseContext: Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}