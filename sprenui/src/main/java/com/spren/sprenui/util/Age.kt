package com.spren.sprenui.util

import java.time.LocalDate
import java.time.Period

object Age {
    fun calculate(birthDate: LocalDate?, currentDate: LocalDate?): Int {
        return if (birthDate != null && currentDate != null) {
            Period.between(birthDate, currentDate).years
        } else {
            0
        }
    }
}