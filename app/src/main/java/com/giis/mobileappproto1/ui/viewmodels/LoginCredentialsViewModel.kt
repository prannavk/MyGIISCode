package com.giis.mobileappproto1.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giis.mobileappproto1.data.models.LoginAuthCredentialDTO
import com.giis.mobileappproto1.data.models.LoginDataDTO
import com.giis.mobileappproto1.data.repositories.CredentialRepository
import com.giis.mobileappproto1.data.sources.remote_api.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

open class LoginCredentialsViewModel @Inject constructor(private val repository: CredentialRepository) :
    BaseViewModel() {

    private var _authId = MutableLiveData<Result<LoginAuthCredentialDTO>>()
    val authId: LiveData<Result<LoginAuthCredentialDTO>>
        get() = _authId

    fun sendCredentialsForVerification(loginData: LoginDataDTO): Unit {
        viewModelScope.launch(super.job) {
            _authId.postValue(
                repository.loginAuth(loginData = loginData)
            )
        }
    }

}