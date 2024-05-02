package com.spren.sprenui.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object Permissions {
    val CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    val EXTERNAL_STORAGE_PERMISSIONS: Array<String>
        get() {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            }

            return arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    fun allPermissionsGranted(
        baseContext: Context,
        permissions: Array<String> = CAMERA_PERMISSIONS
    ) = permissions.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}