package com.giis.mobileappproto1.ui.viewmodels

import com.giis.mobileappproto1.data.repositories.AppRepository
import javax.inject.Inject

open class MessagesViewModel @Inject constructor(private val repository: AppRepository) : BaseViewModel() {

}