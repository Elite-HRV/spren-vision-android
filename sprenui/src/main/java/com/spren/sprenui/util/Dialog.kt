package com.spren.sprenui.util

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.spren.sprenui.R

object Dialog {
    fun create(context: Context): Dialog {
        val dialog = Dialog(context, R.style.Dialog)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_layout)
        return dialog
    }

    fun setUp(
        context: Context,
        dialog: Dialog,
        title: String?,
        message: String?,
        primaryButtonText: String?,
        secondaryButtonText: String?,
        primaryListener: View.OnClickListener?,
        secondaryListener: View.OnClickListener?,
    ) {
        val titleTextView = dialog.findViewById(R.id.title_text) as TextView
        titleTextView.text = title
        val messageTextView = dialog.findViewById(R.id.message_text) as TextView
        messageTextView.text = message
        val primaryButton = dialog.findViewById(R.id.primary_button) as Button
        primaryButton.text = primaryButtonText
        primaryButton.setOnClickListener(primaryListener)
        val secondaryTextView = dialog.findViewById(R.id.secondary_text) as TextView
        if (secondaryButtonText != null) {
            secondaryTextView.visibility = View.VISIBLE
            secondaryTextView.text = secondaryButtonText
            secondaryTextView.setOnClickListener(secondaryListener)
        } else {
            secondaryTextView.visibility = View.GONE
        }
    }
}