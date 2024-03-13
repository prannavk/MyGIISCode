package com.giis.mobileappproto1.ui.viewmodels

import com.giis.mobileappproto1.data.repositories.AppRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val repository: AppRepository) :
    BaseViewModel() {


}