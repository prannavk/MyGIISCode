package com.giis.mobileappproto1.data.models

import com.google.gson.annotations.SerializedName

data class UploadFileApiResponse(
    @SerializedName("filepath")
    val filepathStringsArrayList: ArrayList<FilePath>
)

data class FilePath(
    val fileName: String,
    val fileSize: String,
    val fileExtension: String,
    val fileLocation: String? = null
)

data class FilePathRequest(
    @SerializedName("name")
    val fileName: String,
    @SerializedName("size")
    val fileSize: String,
    @SerializedName("fileType")
    val fileExtension: String,
    @SerializedName("filePath")
    val fileLocation: String
)