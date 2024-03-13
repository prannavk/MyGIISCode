package com.giis.mobileappproto1.utils

import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.ui.activities.CreatePostActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Random
import kotlin.math.abs

// TerraLogic Company -> Santosh and Karthik Sir, Mobile Team Codes

object RealPathUtil {

    fun getRealPath(context: Context, fileUri: Uri): String? {
        return getRealPathFromURIAPI19(context, fileUri)
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author Niks
     */
    @SuppressLint("NewApi")
    fun getRealPathFromURIAPI19(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver.query(
                        uri,
                        arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )
                    cursor!!.moveToNext()
                    val fileName = cursor.getString(0)
                    val path = Environment.getExternalStorageDirectory()
                        .toString() + "/Download/" + fileName
                    if (!TextUtils.isEmpty(path)) {
                        return path
                    }
                } finally {
                    cursor?.close()
                }
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:".toRegex(), "")
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads"),
                    java.lang.Long.valueOf(id)
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                Log.e(CreatePostActivity.dTag, "point - mediaDocument")
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    "document" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                        }
                    }
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            Log.e(CreatePostActivity.dTag, "point - content")
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            Log.e(CreatePostActivity.dTag, "point - file")
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author Niks
     */
    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        Log.e(CreatePostActivity.dTag, "point - data column")

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getFile(context: Context, contentURI: Uri, extension: String): File? {
        try {
            val random = Random()
            val inputStream = context.contentResolver.openInputStream(contentURI)
            val root = Environment.getExternalStorageDirectory().absolutePath
            val fname =
                "GIIS_" + random.nextInt() + System.currentTimeMillis() + extension
            val myDir = File("$root/giis")
            var file = File(myDir, fname)
            if (file.exists()) file.delete()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val out: FileOutputStream?
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fname)
                when (extension) {
                    ".pdf" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    }

                    ".xls" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/xls")
                    }

                    ".xlsx" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/xlsx")
                    }

                    ".csv" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/csv")
                    }

                    ".txt" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/txt")
                    }

                    ".ppt" -> {
                        contentValues.put(
                            MediaStore.MediaColumns.MIME_TYPE,
                            "application/vnd.ms-powerpoint"
                        )
                    }

                    ".pptm" -> {
                        contentValues.put(
                            MediaStore.MediaColumns.MIME_TYPE,
                            "application/vnd.ms-powerpoint.presentation.macroEnabled.12"
                        )
                    }

                    ".doc" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/doc")
                    }

                    ".docx" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/docx")
                    }

                    ".jpg", ".jpeg" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    }

                    ".png" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    }

                    ".mp3" -> {
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                    }
                }
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + File.separator + "GIIS"
                )
                try {
                    val uri = context.contentResolver.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    if (uri != null) {
                        out = context.contentResolver.openOutputStream(uri) as FileOutputStream?
                        val stream = context.contentResolver.openInputStream(contentURI)
                        val byteArrayStream = ByteArrayOutputStream()
                        val buffer = ByteArray(1024)
                        var i: Int
                        while (stream!!.read(buffer, 0, buffer.size)
                                .also { i = it } > 0
                        ) {
                            byteArrayStream.write(buffer, 0, i)
                        }
                        val bytes = byteArrayStream.toByteArray()
                        out!!.write(bytes)
                        out.flush()
                        out.close()
                        val path: String? = getRealPathFromURIAPI19(context, uri)
                        if (path != null) {
                            file = File(path)
                            val fileSizeInBytes = file.length()
                            val fileSizeInKB = fileSizeInBytes / 1024
                            val fileSizeInMB = fileSizeInKB / 1024
                            return if (fileSizeInMB < 10) {
                                file
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.file_less_than_10_mb),
                                    Toast.LENGTH_SHORT
                                ).show()
                                file
                            }
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.file_less_than_10_mb),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Commons.toastIt(context, context.getString(R.string.unable_to_select_file))
                }
            } else {
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    while (inputStream!!.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                    inputStream.close()
                }
                return file
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getFile(pathName: String? = null, context: Context, bitmap: Bitmap?): File? {
        val filename =
            if (pathName != null && pathName != "") {
                pathName
            } else {
                "${context.resources.getString(R.string.app_name)}_" + abs(Random().nextLong()).toString() + System.currentTimeMillis() + ".jpg"
            }
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + context.resources.getString(R.string.app_name)
            )
        }
        val uriNew = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        if (uriNew != null) {
            val outputStream =
                context.contentResolver.openOutputStream(uriNew) as FileOutputStream?
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream?.flush()
            outputStream?.close()
            val path = getRealPath(context, uriNew)
            return if (path != null) {
                val file = File(path)
                file
            } else {
                null
            }
        } else {
            try {
                val root = Environment.getExternalStorageDirectory().absolutePath
                val myDir = File("$root/${context.resources.getString(R.string.app_name)}")
                if (!myDir.exists()) {
                    myDir.mkdirs()
                }
                val file = File(myDir, filename)
                if (file.exists()) file.delete()
                val outputStream = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
                outputStream.close()
                return file
            } catch (e: java.lang.Exception) {
                return null
            }
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


    // Extension on intent

    fun Intent?.getFilePath(context: Context): String {
        return this?.data?.let { data -> getRealPath(context, data) ?: "" } ?: ""
    }

    fun Uri?.getFilePath(context: Context): String {
        return this?.let { uri -> getRealPath(context, uri) } ?: ""
    }

    fun ClipData.Item?.getFilePath(context: Context): String {
        return this?.uri?.getFilePath(context) ?: ""
    }
}