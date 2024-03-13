package com.giis.mobileappproto1.data.sources.remote_api

import com.giis.mobileappproto1.data.models.LoginAuthCredentialDTO
import com.giis.mobileappproto1.data.models.LoginDataDTO
import com.giis.mobileappproto1.data.models.ObtainedOTPDTO
import com.giis.mobileappproto1.data.models.RequestLoginOTPDTO
import com.giis.mobileappproto1.data.models.VerifiedLoginDataDTO
import com.giis.mobileappproto1.data.models.VerifyOTPDTO
import retrofit2.http.Body
import retrofit2.http.POST


interface LoginService {
    @POST("LoginUser")
    suspend fun getLoginId(@Body loginData: LoginDataDTO): Result<LoginAuthCredentialDTO>

    @POST("SendLoginOtp")
    suspend fun obtainLoginOtp(@Body requestLoginOTPDTO: RequestLoginOTPDTO): Result<ObtainedOTPDTO>

    @POST("VerifyLoginUserOPT")
    suspend fun verifyThisLoginOtp(@Body verifyOTPDTO: VerifyOTPDTO): Result<VerifiedLoginDataDTO>
}