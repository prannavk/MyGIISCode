package com.giis.mobileappproto1.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.giis.mobileappproto1.GIISSessionTokenDataManager
import com.giis.mobileappproto1.data.models.PersonSelectTagDTO
import com.giis.mobileappproto1.data.models.UploadFileApiResponse
import com.giis.mobileappproto1.data.repositories.AppRepository
import com.giis.mobileappproto1.data.repositories.MediaRepository
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.ui.activities.CreatePostActivity
import com.giis.mobileappproto1.utils.RealPathUtil
import com.giis.mobileappproto1.utils.RealPathUtil.getFilePath
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import javax.inject.Inject

open class CreatePostViewModel @Inject constructor(
    private val repository: AppRepository,
    private val mediaRepository: MediaRepository
) :
    BaseViewModel() {

    fun fetchLabelOptionsData(context: Context): List<String> =
        repository.fetchLabelsOptions(context, "labels")

    fun fetchPostToOptionsData(context: Context): List<String> =
        repository.fetchLabelsOptions(context, "groups")

    fun fetchAllSelectPersonTagsData(context: Context): List<PersonSelectTagDTO> {
        return repository.fetchSelectTagsData(context) ?: mutableListOf<PersonSelectTagDTO>(
            PersonSelectTagDTO("invalidUrl", "Unable to Show People")
        ).toList()
    }

    fun uploadMedia(
        uriList: MutableList<Uri?>,
        activityContext: Context
    ): MutableLiveData<Result<UploadFileApiResponse>> {
        val actualFilePathList: ArrayList<File> = ArrayList()
        uriList.forEach { uri ->
            uri?.let {
                actualFilePathList.add(
                    File(
                        it.getFilePath(
                            activityContext
                        )
                    )
                )
            }
        }

        val uploadFilePathList: ArrayList<File> = ArrayList()
        if (uriList.size == actualFilePathList.size) {
            for (actualFileIndex in actualFilePathList.indices) {
                val fName = actualFilePathList[actualFileIndex].name
                val extensionOfActualFile = fName.substring(fName.lastIndexOf('.'), fName.length)
                val file = RealPathUtil.getFile(
                    context = activityContext,
                    contentURI = uriList[actualFileIndex]!!,
                    extension = extensionOfActualFile
                )
                file?.let { uploadFilePathList.add(it) }
            }
        } else {
            throw Exception("Extra Files Added Or Some Nulls Present")
        }

        val sessionToken = GIISSessionTokenDataManager(activityContext).getTokenFromSessionData()
            ?: "INVALID_TOKEN"
        val mediaApiResponseMLD = MutableLiveData<Result<UploadFileApiResponse>>()
        viewModelScope.launch {
            mediaApiResponseMLD.postValue(
                mediaRepository.uploadMediaFiles(
                    imageFileList = uploadFilePathList,
                    tokenGot = sessionToken
                )
            )
        }
        return mediaApiResponseMLD
    }

//    private var _mediaApiResponseLD = MutableLiveData<Result<UploadFileApiResponse>>()
//
//    val mediaApiResponseLD: LiveData<Result<UploadFileApiResponse>>
//        get() = _mediaApiResponseLD


}