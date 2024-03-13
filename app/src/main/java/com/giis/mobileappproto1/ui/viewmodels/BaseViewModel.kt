package com.giis.mobileappproto1.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.giis.mobileappproto1.data.repositories.CredentialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import com.giis.mobileappproto1.data.sources.local_room.LoginCredentialDao

open class BaseViewModel : ViewModel() {

    @Inject
    lateinit var credentialRepository: CredentialRepository

    val job = Dispatchers.IO + SupervisorJob()
//
//    fun loadDataByDefault(loginCredential: LoginCredential) {
//        viewModelScope.launch {
//            val existingData = credentialRepository.getAllCredentialData().value
//            if (existingData!!.isEmpty()) {
//                credentialRepository.insertNewCredential(loginCredential)
//            }
//
//        }
//    }


}