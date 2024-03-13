package com.giis.mobileappproto1.utils

import android.content.Context
import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class JsonReadingHelper {

    fun streamReadTest2Json(context: Context) {
        try {
            val inputStream = context.assets.open("test_2.json")
            val sourceReader =
                JsonReader(BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)))
            sourceReader.peek()
            traverseWith(sourceReader)
            sourceReader.close()
        } catch (ex: IOException) {
            ex.printStackTrace(); Log.e(globalTag, "IO Issue")
        } catch (es: Exception) {
            Log.e(globalTag, "Please check your code")
        }
    }

    private fun traverseWith(reader: JsonReader) {
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.peek()) {
                    JsonToken.NAME -> {
                        when (reader.nextName()) {
                            "pName", "pJob", "pBloodGroup" -> reader.nextString()
                                .also { Log.e(globalTag, it) }

                            "pSal" -> reader.nextInt().also { Log.e(globalTag, "$it") }
                            "pCalls", "pCod", "pLocations" -> handleSimpleArray(reader)
                            else -> {
                                Log.e(globalTag, "Check Token Name")
                            }
                        }
                    }

                    JsonToken.END_OBJECT -> {
                        Log.e(globalTag, "Object Ends"); reader.endObject(); break; }

                    JsonToken.END_DOCUMENT -> {
                        Log.e(globalTag, "Document Ended!!!"); break; }

                    else -> {
                        Log.e(globalTag, "???"); break; }
                }
            }
        } catch (ex: Exception) {
            Log.e(globalTag, "Please check your code and json"); }
    }

    private fun handleSimpleArray(reader: JsonReader) {
        try {
            reader.beginArray()
            while (true) {
                when (reader.peek()) {
                    JsonToken.END_ARRAY -> {
                        Log.e(globalTag, "Array End"); reader.endArray(); break; }

                    JsonToken.NUMBER -> reader.nextInt().also { Log.e(globalTag, "$it") }
                    JsonToken.STRING -> reader.nextString().also { Log.e(globalTag, it) }
                    JsonToken.NULL -> reader.skipValue().also { Log.e(globalTag, "Null occurred") }
                    JsonToken.END_DOCUMENT -> {
                        Log.e(globalTag, "Document Ended!!!"); break; }

                    else -> {
                        Log.e(globalTag, "???"); break; }
                }
            }
        } catch (ex: Exception) {
            Log.e(globalTag, "Please check your code and json"); }
    }

}