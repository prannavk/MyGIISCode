package com.giis.mobileappproto1.ui.adapters

import androidx.databinding.BindingAdapter
import com.giis.mobileappproto1.data.models.DateStatus
import com.giis.mobileappproto1.data.models.NDateCategory

class NotificationBindingAdapter {
    companion object {

        @BindingAdapter("dateStatusData")
        @JvmStatic
        fun androidx.appcompat.widget.AppCompatTextView.configurePostedStatusString(status: DateStatus) {
            this.text = when (status.category) {
                NDateCategory.TODAY -> if (status.difference == 1) "${status.difference} hour ago" else "${status.difference} hours ago"
                NDateCategory.THIS_WEEK -> if (status.difference == 1) "${status.difference} day ago" else "${status.difference} days ago"
                NDateCategory.PREVIOUS -> if (status.difference == 1) "${status.difference} week ago" else "${status.difference} weeks ago"
            }
        }

        @BindingAdapter("submitTitleText")
        @JvmStatic
        fun androidx.appcompat.widget.AppCompatTextView.checkTitle(setTitle: String) {
            this.text = when (setTitle) {
                "TODAY" -> "Today"
                "THIS_WEEK" -> "This Week"
                else -> "Previous"
            }
        }

    }
}