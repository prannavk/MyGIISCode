package com.giis.mobileappproto1.data.sources.remote_api

import com.giis.mobileappproto1.data.models.UploadFileApiResponse
import com.giis.mobileappproto1.data.sources.remote_api.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MediaService {

    @Multipart
    @POST(ServiceBuilder.UPLOAD_FILE_BUCKET_NAME_EXTENSION)
    suspend fun uploadFiles(
        @Part image: Array<MultipartBody.Part?>,
        @Part("jwtToken") jwtToken: RequestBody?,
        @Part("AwsBucketName") awsBucketName: RequestBody?,
        @Query("jwtToken") token: String?,
        @Query("AwsBucketName") bucketName: String?
    ): Result<UploadFileApiResponse>

}