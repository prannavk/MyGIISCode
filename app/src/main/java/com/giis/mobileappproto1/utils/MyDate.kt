package com.giis.mobileappproto1.utils

import com.giis.mobileappproto1.data.models.DateStatus
import com.giis.mobileappproto1.data.models.NDateCategory
import java.util.Date
import java.util.concurrent.TimeUnit

data class MyDate(val longTime: Long) {
    operator fun minus(other: MyDate): DateStatus {
        val recentDate: Date
        val pastDate: Date
        if (this.longTime > other.longTime) {
            recentDate = Date(this.longTime)
            pastDate = Date(other.longTime)
        } else {
            recentDate = Date(other.longTime)
            pastDate = Date(this.longTime)
        }
        val differenceInMillis = recentDate.time - pastDate.time
        val hours = TimeUnit.MILLISECONDS.toHours(differenceInMillis)
        val days = TimeUnit.MILLISECONDS.toDays(differenceInMillis)
        val weeks = TimeUnit.MILLISECONDS.toDays(differenceInMillis) / 7
        val months = TimeUnit.MILLISECONDS.toDays(differenceInMillis) / 30 // approx
        return when {
            months >= 1 -> DateStatus(months.toInt(), NDateCategory.PREVIOUS)
            weeks >= 1 -> DateStatus(weeks.toInt(), NDateCategory.PREVIOUS)
            days >= 1 -> DateStatus(days.toInt(), NDateCategory.THIS_WEEK)
            else -> DateStatus(hours.toInt(), NDateCategory.TODAY)
        }
    }
}
