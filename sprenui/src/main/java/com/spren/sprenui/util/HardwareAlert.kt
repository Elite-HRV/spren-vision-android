package com.spren.sprenui.util

import android.app.Activity
import android.view.View

class HardwareAlert(val activity: Activity) {
    private var dialog: android.app.Dialog = Dialog.create(activity)
    fun show() {
        val alertTitle = "Your device is not compatible"
        val alertMessage =
            "Unfortunately, the hardware in your device is not powerful enough to run the Spren app."
        val alertPrimaryButtonText = "Close App"
        val alertOnPrimaryButtonTap: ((View) -> Unit) = { _ ->
            dialog.dismiss()
            activity.finish()
        }
        Dialog.setUp(
            activity,
            dialog,
            alertTitle,
            alertMessage,
            alertPrimaryButtonText,
            null,
            alertOnPrimaryButtonTap,
            null
        )
        dialog.show()
    }
}