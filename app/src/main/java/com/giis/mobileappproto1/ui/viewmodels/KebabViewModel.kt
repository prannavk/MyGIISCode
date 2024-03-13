package com.giis.mobileappproto1.ui.viewmodels

import android.content.Context
import com.giis.mobileappproto1.data.repositories.AppRepository
import javax.inject.Inject

class KebabViewModel @Inject constructor(private val repository: AppRepository) :
    BaseViewModel() {

    fun updateAsRead(context: Context, clickedOnNotId: Int, b: Boolean) {
        repository.updateThisJSONNotificationDTO(
            context = context,
            targetNotId = clickedOnNotId,
            readFlag = true
        )
    }

}