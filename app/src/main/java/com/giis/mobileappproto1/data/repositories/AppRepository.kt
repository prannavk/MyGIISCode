package com.giis.mobileappproto1.data.repositories

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.giis.mobileappproto1.data.models.FeedPostDTO
import com.giis.mobileappproto1.data.models.NoticeDTO
import com.giis.mobileappproto1.data.models.NotificationDTO
import com.giis.mobileappproto1.data.models.PersonSelectTagDTO
import com.giis.mobileappproto1.data.sources.local_room.SavePostDAO
import com.giis.mobileappproto1.data.sources.remote_api.GenericService
import com.giis.mobileappproto1.utils.globalTag
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val apiEndPoint: GenericService,
    private val postsDao: SavePostDAO
) {

    suspend fun persistThisPost(postDTO: FeedPostDTO) {
        this.postsDao.persistThisFeedPost(postDTO)
    }

    fun getAllPersistedPosts(): LiveData<List<FeedPostDTO>> = this.postsDao.fetchAllSavedPosts()

    suspend fun removeThisPost(feedPostDTO: FeedPostDTO) {
        this.postsDao.deleteThisFeedPost(feedPostDTO)
    }

//    suspend fun confirmLoginDataForToken(verifyOTPDTO: VerifyOTPDTO): Result<VerifiedLoginDataDTO> =
//        apiInterface.verifyThisLoginOtp(verifyOTPDTO = verifyOTPDTO)

    private fun loadJsonIntoByteArray(context: Context, fileName: String): ByteArray {
        return try {
            val inputStream = context.assets.open(fileName)
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            buffer
        } catch (ex: IOException) {
            ex.printStackTrace()
            ByteArray(1) { 0 }
        }
    }

    fun fetchNoticeDTOList(context: Context): List<NoticeDTO> {
        val buffer = this.loadJsonIntoByteArray(context, "e_noticeboard_json_data.json")
        val jsonArray = JSONArray(String(buffer, StandardCharsets.UTF_8))
        val max = jsonArray.length()
        val noticeDTOList = mutableListOf<NoticeDTO>()
        for (i in 0 until max step 1) {
            val jsonObject = jsonArray.getJSONObject(i)
            val notice = NoticeDTO(
                noticeDescription = jsonObject.getString("noticeDescription"),
                noticeSource = jsonObject.getString("noticeSource"),
                noticeTitle = jsonObject.getString("noticeTitle"),
                noticeType = jsonObject.getString("noticeType")
            )
            noticeDTOList.add(notice)
        }
        return noticeDTOList.toList()
    }

    fun fetchAllNotificationsDTOList(context: Context): List<NotificationDTO> {
        val buffer = this.loadJsonIntoByteArray(context, "notifications.json")
        val jsonArray = JSONArray(String(buffer, StandardCharsets.UTF_8))
        val max = jsonArray.length()
        val notificationDTOList = mutableListOf<NotificationDTO>()
        for (i in 0 until max step 1) {
            val jsonObject = jsonArray.getJSONObject(i)
            val notice = NotificationDTO(
                notificationMessage = jsonObject.getString("notificationMessage"),
                notificationPostedTime = jsonObject.getLong("notificationPostedTime"),
                isNotificationRequest = jsonObject.getBoolean("isNotificationRequest"),
                isNotificationTag = jsonObject.getBoolean("isTag"),
                isRead = jsonObject.getBoolean("isRead"),
                notId = jsonObject.getInt("notId")
            )
            notificationDTOList.add(notice)
        }
        return notificationDTOList.toList()
    }

    fun updateThisJSONNotificationDTO(context: Context, targetNotId: Int, readFlag: Boolean): Unit {
        val buffer = this.loadJsonIntoByteArray(context, "notifications.json")
        val jsonArray = JSONArray(String(buffer, StandardCharsets.UTF_8))
        val max = jsonArray.length()
        for (i in 0 until max) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (jsonObject.getInt("notId") == targetNotId) {
                jsonObject.put("isRead", readFlag)
                break
            }
        }
    }

    fun fetchLabelsOptions(context: Context, arrayString: String): List<String> {
        val buffer = this.loadJsonIntoByteArray(context, "create_options_data.json")
        val jsonObject = JSONObject(String(buffer, StandardCharsets.UTF_8))
        val labelsData = jsonObject.getJSONArray(arrayString)
        val labelsList = mutableListOf<String>()
        for (i in 0 until labelsData.length()) {
            labelsList.add(labelsData[i] as String)
        }
        return labelsList.toList()
    }

    fun fetchSelectTagsData(context: Context): List<PersonSelectTagDTO>? {
        return try {
            val inputStream = (context as FragmentActivity).assets.open("tag_people.json")
            val reader = inputStream.reader()
            val listType = object : TypeToken<List<PersonSelectTagDTO>>() {}.type
            val personList: List<PersonSelectTagDTO> = Gson().fromJson(reader, listType)
            reader.close()
            inputStream.close()
//            val buffer = this.loadJsonIntoByteArray(context, "tag_people.json")
//            val jsonArray = JSONArray(String(buffer, StandardCharsets.UTF_8))
//            val personList = mutableListOf<PersonSelectTagDTO>()
//            for (i in 0 until jsonArray.length()) {
//                val jsonObject = jsonArray.getJSONObject(i)
//                val dto = PersonSelectTagDTO(
//                    pfpUrl = jsonObject.getString("pfpUrl"),
//                    tagName = jsonObject.getString("tagName")
//                )
//                personList.add(dto)
//            }
//            personList.toList()
            personList
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("json Gson Error", "Error while extracting and parsing local tags json")
            null
        }
    }

    fun testGson() {
        val gsonObject: Gson = GsonBuilder().setPrettyPrinting().setLenient().create()
        val g = GsonConverterFactory.create(gsonObject)
    }

    private fun playWithJson(context: Context) {
        try {
            val inputStream = context.assets.open("test_2.json")
            val sourceReader = JsonReader(BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)))
            sourceReader.peek()
            traverseWith(sourceReader)
            sourceReader.close()
        } catch (ex: IOException) { ex.printStackTrace(); Log.e(globalTag, "IO Issue")
        } catch (es: Exception) { Log.e(globalTag, "Please check your code") }
    }

    private fun traverseWith(reader: JsonReader) {
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.peek()) {
                    JsonToken.NAME -> {
                        when (reader.nextName()) {
                            "postText", "postCreatorName", "postCreatorDetails" -> reader.nextString().also { Log.e(globalTag, it) }
                            "postId" -> reader.nextInt().also { Log.e(globalTag, "$it") }
                            "pCalls", "pCod", "pLocations" -> handleSimpleArray(reader)
                            else -> { Log.e(globalTag, "Check Token Name") }
                        }
                    }
                    JsonToken.END_OBJECT -> { Log.e(globalTag, "Object Ends"); reader.endObject(); break; }
                    JsonToken.END_DOCUMENT -> { Log.e(globalTag, "Document Ended!!!"); break; }
                    else -> { Log.e(globalTag, "???"); break; }
                }
            }
        } catch (ex: Exception) { Log.e(globalTag, "Please check your code and json"); }
    }

    private fun handleSimpleArray(reader: JsonReader) {
        try {
            reader.beginArray()
            arrayLoop@ while (true) {
                when (reader.peek()) {
                    JsonToken.END_ARRAY -> { Log.e(globalTag, "Array End"); reader.endArray(); break@arrayLoop; }
                    JsonToken.NUMBER -> reader.nextInt().also { Log.e(globalTag, "$it") }
                    JsonToken.STRING -> reader.nextString().also { Log.e(globalTag, it) }
                    JsonToken.NULL -> reader.skipValue().also { Log.e(globalTag, "Null occurred") }
                    JsonToken.END_DOCUMENT -> { Log.e(globalTag, "Document Ended!!!"); break@arrayLoop; }
                    else -> { Log.e(globalTag, "???"); break; }
                }
            }
        } catch (ex: Exception) { Log.e(globalTag, "Please check your code and json"); }
    }


}