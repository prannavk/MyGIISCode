package com.giis.mobileappproto1.data.sources.local_room

import androidx.room.TypeConverter
import com.giis.mobileappproto1.data.models.LabelType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Convertors {

    // Converters for property -> val postCreationDateTime: Date
    @TypeConverter
    fun fromNullableDateToLong(value: Date?): Long? = value?.let { it.time } ?: kotlin.run { null }

    @TypeConverter
    fun fromNullableLongToDate(value: Long?): Date? = value?.let { Date(it) } ?: kotlin.run { null }

    // Converters for property -> val postCreationDateTime: Date
    @TypeConverter
    fun fromDateToLong(value: Date): Long = value.time

    @TypeConverter
    fun fromLongToDate(value: Long): Date = Date(value)

    // Converters for property -> val postLabel: LabelType
    @TypeConverter
    fun fromEnum(value: LabelType): String {
        return value.name
    }

    @TypeConverter
    fun toEnum(value: String): LabelType {
        return enumValueOf(value)
    }

    // Converters for property -> val postPollData: HashMap<String, Int>?
    @TypeConverter
    fun fromHashMap(map: HashMap<String, Int>?): String? {
        // Convert HashMap to a JSON string
        return map?.let {
            val gson = Gson()
            gson.toJson(map)
        }
    }

    @TypeConverter
    fun toHashMap(json: String?): HashMap<String, Int>? {
        // Convert JSON string back to a HashMap
        return json?.let {
            val gson = Gson()
            val type = object : TypeToken<HashMap<String, Int>>() {}.type
            gson.fromJson<HashMap<String, Int>>(json, type)
        }
    }

    // Converters for properties -> val postTagsList: List<String>?, val postImageList: List<String>?
    @TypeConverter
    fun fromListString(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toListString(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

}