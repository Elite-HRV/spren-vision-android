package com.spren.sprenui.util

import android.content.Context
import com.spren.sprenui.ui.bodycomp.WeightBodyCompFragment

object Units {
    fun pxFromDp(context: Context?, dp: Int): Int {
        val density = if (context != null) context!!.resources.displayMetrics.density else 1F
        return (dp * density).toInt()
    }

    /**
    1 ft = 0.3048 m
    1 in = 0.0254 m
     */
    fun feetToCm(feet: Int, inches: Int): Double {
        val meters = feet * 0.3048 + inches * 0.0254

        return meters * 100
    }

    /**
    1 lbs = 1 x 0.45359237 kg
     */
    fun lbsToKg(lbs: Float): Double {
        return lbs * 0.45359237
    }

    /**
    1 kg / 0.45359237 = 2.2046226218 lb
     */
    fun kgToLbs(kg: Double): Double {
        return kg / 0.45359237
    }

    fun isMetricSettingsUsed(unit: String): Boolean {
        return unit == WeightBodyCompFragment.WEIGHT_METRIC_KG
    }

    // Based on user input convert the result
    fun convertToImperialIfNeeded(lbsOrKg: Double?, unit: String): Double? {
        if (lbsOrKg == null) {
            return null
        }
        if (isMetricSettingsUsed(unit)) {
            return lbsOrKg
        }

        return this.kgToLbs(lbsOrKg)
    }
}