package com.speakbyhand.datacollectorclient.core

import android.content.ContentValues
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GestureDataApiService {
    // for API
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType();
    private val host = "http://192.168.1.4:5000"

    fun postGestureDataRecording(
        gestureName: String,
        accData: String,
        gyroData: String,
        linearData: String
    ) {
        val timestamp =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
        val data = JSONObject(
            mapOf(
                "gestureName" to gestureName,
                "acc" to accData,
                "gyro" to gyroData,
                "linear" to linearData,
            )
        )
        val request = Request
            .Builder()
            .url("${host}/data")
            .post(data.toString().toRequestBody(jsonMediaType))
            .build()
        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }

                        println(response.body.string())
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun getGestureDataPrediction(accData: String, gyroData: String, linearData: String, callback: (String) -> Unit): Unit {
        val data = JSONObject(
            mapOf(
                "acc" to accData,
                "gyro" to gyroData,
                "linear" to linearData,
            )
        )
        val request = Request.Builder().url("${host}/prediction")
            .post(data.toString().toRequestBody(jsonMediaType)).build()

        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }
                        val value = response.body.string()
                        println(value)
                        callback(value)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()

    }
}