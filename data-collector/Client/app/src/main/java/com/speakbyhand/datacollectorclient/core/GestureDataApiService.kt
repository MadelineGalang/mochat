package com.speakbyhand.datacollectorclient.core

import android.content.ContentValues
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class GestureDataApiService {
    // for API
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType();


    fun postGestureData(gestureName:String, accData: String, gyroData: String, linearData: String) {
        val timestamp = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
        val data = JSONObject(
            mapOf(
                "gestureName" to gestureName,
                "acc" to accData,
                "gyro" to gyroData,
                "linear" to linearData,
            )
        )
        val request = Request.Builder().url("http://192.168.1.7:5000/data")
            .post(data.toString().toRequestBody(jsonMediaType)).build()

        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body
                        Log.d(ContentValues.TAG, "onResponse: " + responseBody.string())
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()


    }
}