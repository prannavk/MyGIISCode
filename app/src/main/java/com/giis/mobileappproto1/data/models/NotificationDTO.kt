package com.giis.mobileappproto1.data.models

import java.util.Date
import java.util.concurrent.TimeUnit

data class NotificationDTO(
    val notId: Int,
    val notificationMessage: String,
    val notificationPostedTime: Long,
    val isNotificationRequest: Boolean,
    val isNotificationTag: Boolean,
    val isRead: Boolean
) {
    val notifType: NType
    var notificationErrorFlag = false
    var notificationPostedTimeStatus = DateStatus(1, NDateCategory.PREVIOUS)


    init {
        notifType = enforceNotificationType()
    }

    private fun enforceNotificationType(): NType {
        return if (!this.isNotificationRequest && !this.isNotificationTag) NType.GENERAL
        else if (!this.isNotificationRequest && this.isNotificationTag) NType.TAGGED
        else if (this.isNotificationRequest && !this.isNotificationTag) NType.REQUEST
        else NType.GENERAL.also { this.notificationErrorFlag = true }
    }

}

enum class NType {
    REQUEST,
    GENERAL,
    TAGGED
}

data class DateStatus(
    val difference: Int,
    val category: NDateCategory
)

enum class NDateCategory(val priority: Int) {
    TODAY(priority = 7),
    THIS_WEEK(priority = 8),
    PREVIOUS(priority = 9)
}

// If Today : Show difference in hours
// If This Week: Show difference in days
// If This Month: Show difference in days
// If Previous: No Need to Show difference Just keep "Previous"

