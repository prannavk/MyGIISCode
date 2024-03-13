package com.giis.mobileappproto1.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.data.models.EntityLoginStampDTO
import com.giis.mobileappproto1.data.models.VerifiedLoginDataDTO
import com.giis.mobileappproto1.data.models.VerifyOTPDTO
import com.giis.mobileappproto1.data.repositories.CredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
open class NewBottomSheetDialogViewModel @Inject constructor(private val repository: CredentialRepository) : BaseViewModel() {

    fun confirmLoginOtp(verifyOTPDTO: VerifyOTPDTO): MutableLiveData<Result<VerifiedLoginDataDTO>> {
        val confirmedLoginData = MutableLiveData<Result<VerifiedLoginDataDTO>>()
        viewModelScope.launch(super.job) {
            confirmedLoginData.postValue(
                // when(repository.confirmLogin(verifyOTPDTO = verifyOTPDTO)){
                repository.confirmLogin(verifyOTPDTO = verifyOTPDTO)
            )
        }
        return confirmedLoginData
    }

    fun insertLoginInfo(loginStamp: EntityLoginStampDTO): Unit {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNewLoginStamp(loginStamp)
            Log.e("verificationData", "LoginStamp Insertion: ${loginStamp.toString()}")
        }
    }
}