package com.spren.sprenui.util

import android.content.Context

fun Context.getThemeId(): Int {
    try {
        val wrapper: Class<*> = Context::class.java
        val method = wrapper.getMethod("getThemeResId")
        method.isAccessible = true
        return (method.invoke(this) as Int)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return 0
}