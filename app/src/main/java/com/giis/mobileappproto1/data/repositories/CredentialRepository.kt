package com.giis.mobileappproto1.data.repositories

import androidx.lifecycle.LiveData
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.data.models.EntityLoginCredentialDTO
import com.giis.mobileappproto1.data.models.EntityLoginStampDTO
import com.giis.mobileappproto1.data.models.LoginAuthCredentialDTO
import com.giis.mobileappproto1.data.models.LoginDataDTO
import com.giis.mobileappproto1.data.models.ObtainedOTPDTO
import com.giis.mobileappproto1.data.models.RequestLoginOTPDTO
import com.giis.mobileappproto1.data.models.VerifiedLoginDataDTO
import com.giis.mobileappproto1.data.models.VerifyOTPDTO
import com.giis.mobileappproto1.data.sources.local_room.LoginCredentialDao
import com.giis.mobileappproto1.data.sources.remote_api.LoginService
import javax.inject.Inject

class CredentialRepository @Inject constructor(
    private val credentialDao: LoginCredentialDao,
    private val apiInterface: LoginService
) {

    // Implementation of API Calls and Room Access

    suspend fun confirmLogin(verifyOTPDTO: VerifyOTPDTO): Result<VerifiedLoginDataDTO> =
        apiInterface.verifyThisLoginOtp(verifyOTPDTO = verifyOTPDTO)

    suspend fun loginAuth(loginData: LoginDataDTO): Result<LoginAuthCredentialDTO> =
        apiInterface.getLoginId(loginData = loginData)

    suspend fun requestTheLoginOtpFor(
        requestLoginOTPDTO: RequestLoginOTPDTO
    ): Result<ObtainedOTPDTO> =
        apiInterface.obtainLoginOtp(requestLoginOTPDTO = requestLoginOTPDTO)


    suspend fun insertNewLoginStamp(loginStamp: EntityLoginStampDTO): Unit {
        credentialDao.insertLoginStamp(loginStamp)
    }

    fun getAllLoginStamps(): LiveData<List<EntityLoginStampDTO>> {
        return credentialDao.fetchAllLoginStamps()
    }

    fun getAllCredentialData(): LiveData<List<EntityLoginCredentialDTO>> = credentialDao.fetchAllCredentialData()

    suspend fun insertNewCredential(newCredential: EntityLoginCredentialDTO) {
        credentialDao.insertLoginCredential(newCredential)
    }

}
