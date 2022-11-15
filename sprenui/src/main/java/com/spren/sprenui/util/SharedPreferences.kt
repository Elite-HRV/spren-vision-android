package com.spren.sprenui.util

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity

object SharedPreferences {
    @Throws(RuntimeException::class)
    fun getSharedPreferences(activity: FragmentActivity?): SharedPreferences {
        return activity?.getPreferences(Context.MODE_PRIVATE) ?: throw RuntimeException("Cannot access to SharedPreferences")
    }
}