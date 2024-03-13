package com.giis.mobileappproto1.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.data.models.ObtainedOTPDTO
import com.giis.mobileappproto1.data.models.RequestLoginOTPDTO
import com.giis.mobileappproto1.data.repositories.CredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
open class OTPVerifyViewModel @Inject constructor(
    private val repository: CredentialRepository
//    private val authCredential: LoginAuthCredential
) : BaseViewModel() {

    fun obtainLoginOTPFor(requestLoginOTPDTO: RequestLoginOTPDTO): MutableLiveData<Result<ObtainedOTPDTO>> {
        val obtainedOtpResult = MutableLiveData<Result<ObtainedOTPDTO>>()
        viewModelScope.launch(super.job) {
            val loginOtpDeferred = async {
                obtainedOtpResult.postValue(
                    repository.requestTheLoginOtpFor(requestLoginOTPDTO = requestLoginOTPDTO)
                )
            }
            if (requestLoginOTPDTO.isEmail) mockSendOtpToEmail(loginOtpDeferred) else mockSendOtpToMobile(
                loginOtpDeferred
            )
        }
        return obtainedOtpResult
    }

    private suspend fun mockSendOtpToEmail(deferred: Deferred<Unit>) {
        deferred.await()
        delay(400)
        Log.e("verificationData", "Email Sent")
    }

    private suspend fun mockSendOtpToMobile(deferred: Deferred<Unit>) {
        deferred.await()
        delay(300)
        Log.e("verificationData", "Message Sent")
    }

}