package com.giis.mobileappproto1.data.repositories

import android.util.Log
import com.giis.mobileappproto1.data.models.UploadFileApiResponse
import com.giis.mobileappproto1.data.sources.remote_api.ServiceBuilder
import com.giis.mobileappproto1.data.sources.remote_api.MediaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.ui.activities.CreatePostActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class MediaRepository @Inject constructor(
    private val mediaApiEndPoint: MediaService
) {

    suspend fun uploadMediaFiles(
        imageFileList: ArrayList<File>,
        tokenGot: String
    ): Result<UploadFileApiResponse> {
        Log.e(CreatePostActivity.dTag, "fun uploadMediaFiles executed")
        return withContext(Dispatchers.IO) {
            val imageParts: Array<MultipartBody.Part?> = arrayOfNulls(imageFileList.size)
            for (index in imageParts.indices) {
                val imageFile = File(imageFileList[index].path)
                val requestBody: RequestBody = imageFile.asRequestBody("*/*".toMediaTypeOrNull())
                imageParts[index] =
                    MultipartBody.Part.createFormData("files", imageFile.name, requestBody)
            }
            val tokenFormData: RequestBody =
                tokenGot.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val awsBucketNameFormData: RequestBody =
                ServiceBuilder.UPLOAD_FILE_BUCKET_NAME_EXTENSION.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val tokenParam = tokenGot as String
            mediaApiEndPoint.uploadFiles(
                image = imageParts,
                jwtToken = tokenFormData,
                awsBucketName = awsBucketNameFormData,
                token = tokenParam,
                bucketName = ServiceBuilder.UPLOAD_FILE_BUCKET_NAME_EXTENSION
            )
        }
    }


}