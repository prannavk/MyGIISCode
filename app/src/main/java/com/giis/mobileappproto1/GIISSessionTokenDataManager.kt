package com.giis.mobileappproto1

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.giis.mobileappproto1.data.models.DateStatus
import com.giis.mobileappproto1.data.models.NDateCategory
import com.giis.mobileappproto1.data.models.VerifiedLoginDataDTO
import com.giis.mobileappproto1.utils.MyDate
import com.giis.mobileappproto1.utils.globalTag
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.Date


class GIISSessionTokenDataManager(
    val context: Context,
) {

    private data class SessionData(
        val isDataPut: Boolean,
        val verifiedDataString: String,
        val dataCreationLong: Long
    )

    private val prefFileName = "ep" // k95711aE#4d5
    private val savedPreferenceKey1 = "sd"
    private var encryptedPrefs: SharedPreferences?
    private val dtoTypeToken = object : TypeToken<VerifiedLoginDataDTO>() {}.type
    var sessionCreationErrorFlag: Boolean = false

    init {
        encryptedPrefs = createBasicSessionFile()
    }

    private fun gsonHelper(): Gson = GsonBuilder().setPrettyPrinting().setLenient().create()

    private fun createBasicSessionFile(): SharedPreferences? {

        fun handleException(ex: Exception, exString: String) {
            ex.printStackTrace()
            Log.e(globalTag, "$exString Exception thrown")
            sessionCreationErrorFlag = true
        }

        try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val enc = EncryptedSharedPreferences.create(
                prefFileName,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return@createBasicSessionFile enc.also {
                Log.e(
                    globalTag,
                    "Session Created Successfully $it"
                )
            }
        } catch (e: GeneralSecurityException) {
            handleException(e, "Security")
            return@createBasicSessionFile null
        } catch (e: IOException) {
            handleException(e, "IO")
            return@createBasicSessionFile null
        } catch (ex: Exception) {
            handleException(ex, "General")
            return@createBasicSessionFile null
        }
    }

    // functions exposed to be used

    fun createAndSaveNewSessionWithPreference(
        verifiedLoginDataDTO: VerifiedLoginDataDTO,
        preference: Boolean
    ): Boolean {
        return if (!sessionCreationErrorFlag) {
            encryptedPrefs?.let {
                it.edit().clear().apply()
                val dtoJsonString = gsonHelper().toJson(verifiedLoginDataDTO)
                val sessionDataString =
                    gsonHelper().toJson(SessionData(preference, dtoJsonString, Date().time))
                Log.e(globalTag, "SC pref : $preference")
                it.edit().putString(savedPreferenceKey1, sessionDataString).apply()
                Log.e(globalTag, "SC inserted at ${Date().time}")
                true
            } ?: run {
                sessionCreationErrorFlag = true; Log.e(
                globalTag,
                "Cannot create and save preferences in a null Preference Object"
            ); false
            }
        } else false
    }

    fun validateSavedSessionAndCheckPref(at: Int): Boolean =
        if (!sessionCreationErrorFlag) {
            encryptedPrefs?.let {
                if (it.contains(savedPreferenceKey1)) {
                    val savedString: String? = it.getString(savedPreferenceKey1, null)
                    savedString?.let savedLet@{ savedSessionString ->
                        val extractedSessionData =
                            gsonHelper().fromJson(savedSessionString, SessionData::class.java)
                        Log.e(globalTag, "SV 2 ${extractedSessionData.dataCreationLong}")
                        val sessionDataDateInfo: DateStatus =
                            MyDate(Date().time) - MyDate(extractedSessionData.dataCreationLong)
                        val verifiedDTO: VerifiedLoginDataDTO? = gsonHelper().fromJson(
                            extractedSessionData.verifiedDataString,
                            dtoTypeToken
                        ) as? VerifiedLoginDataDTO
                        val dataValidation: Boolean =
                            verifiedDTO?.let dataLet@{ dto -> return@dataLet (dto.token != null && dto.token!!.length > 1) }
                                ?: run dataRun@{ return@dataRun false }
                        val dateValidation: Boolean =
                            (sessionDataDateInfo.category == NDateCategory.TODAY) && (sessionDataDateInfo.difference <= 3)
                        return@savedLet if (at == 1) (dataValidation && dateValidation && extractedSessionData.isDataPut) else (dataValidation && dateValidation)
                    } ?: run { Log.e(globalTag, "SV 1"); false }
                } else {
                    Log.e(globalTag, "SV 0 $it")
                    false
                }
            } ?: run { false }
        } else false

    fun getTokenFromSessionData(): String? =
        if (validateSavedSessionAndCheckPref(2)) {
            val savedSessionDataString = encryptedPrefs!!.getString(savedPreferenceKey1, null)
            val tokenString = savedSessionDataString?.let { dataString ->
                val extractedSessionData =
                    gsonHelper().fromJson(dataString, SessionData::class.java)
                val verifiedDTO: VerifiedLoginDataDTO? = gsonHelper().fromJson(
                    extractedSessionData.verifiedDataString,
                    dtoTypeToken
                ) as? VerifiedLoginDataDTO
                verifiedDTO!!.token
            } ?: run { null }
            tokenString
        } else {
            null
        }

    fun getVerifiedSessionDTO(): VerifiedLoginDataDTO? {
        return if (validateSavedSessionAndCheckPref(2)) {
            val savedSessionDataString = encryptedPrefs!!.getString(savedPreferenceKey1, null)
            val tokenString = savedSessionDataString?.let { dataString ->
                val extractedSessionData =
                    gsonHelper().fromJson(dataString, SessionData::class.java)
                val verifiedDTO: VerifiedLoginDataDTO? = gsonHelper().fromJson(
                    extractedSessionData.verifiedDataString,
                    dtoTypeToken
                ) as? VerifiedLoginDataDTO
                verifiedDTO!!
            } ?: run { null }
            tokenString
        } else {
            null
        }
    }


}