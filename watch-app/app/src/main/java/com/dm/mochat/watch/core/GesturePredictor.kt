package com.dm.mochat.watch.core

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

enum class Gesture{
    Up,
    Down,
    Left,
    Right,
    CircleIn,
    CircleOut,
    Unknown
}

interface GesturePredictor {
    fun predictGesture(data: GestureData, callback: (Gesture) -> Unit);
}

abstract class ApiGestureDetector(private val endpoint: String): GesturePredictor{
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType();
    private val host = "http://192.168.1.10:5000"


    override fun predictGesture(
        data: GestureData,
        callback: (Gesture) -> Unit
    ) {
        val data = JSONObject(
            mapOf(
                "acc" to data.accelerometerData.joinToString("\n") { it.toCsvString() },
                "gyro" to data.gyroscopeData .joinToString("\n") { it.toCsvString() },
                "linear" to data.linearData.joinToString("\n") { it.toCsvString() },
            )
        )
        val request = Request.Builder().url("${host}/${endpoint}")
            .post(data.toString().toRequestBody(jsonMediaType)).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for ((name, value) in response.headers) {
                    println("$name: $value")
                }
                val value = response.body.string()
                println(value)
                callback(navigationGestureStringToEnum(value))
            }

        })
    }

    private fun navigationGestureStringToEnum(gestureString: String): Gesture {
        return when (gestureString) {
            "left" -> Gesture.Left
            "right" -> Gesture.Right
            "up" -> Gesture.Up
            "down" -> Gesture.Down
            "circle_in" -> Gesture.CircleIn
            "circle_out" -> Gesture.CircleOut
            else -> Gesture.Unknown
        }
    }
}

class ApiMessageGestureDetector : ApiGestureDetector("prediction"){}

class ApiNavigationGestureDetector : ApiGestureDetector("prediction"){}
