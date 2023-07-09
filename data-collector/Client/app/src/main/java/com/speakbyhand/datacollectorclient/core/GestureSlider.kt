package com.speakbyhand.datacollectorclient.core

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.floor

class GestureSlider(
    private val sensorManager: SensorManager,
    sensorType: Int,
    var onPrevious: () -> Unit,
    var onNext: () -> Unit,
    var onUpdate: () -> Unit,
    var sensitivity: Float = 5f
) {
    var currentIndex: Int = 0
        get() { return floor(distanceFromOrigin / sensitivity).toInt() }
        private set;

    var distanceFromOrigin: Float = 0f
        private set
    private var timeStartMilliseconds: Long = 0
    private var previousTickMilliseconds: Long = 0
    private val gestureSensor: Sensor = sensorManager.getDefaultSensor(sensorType)
    private val sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            onTick(sensorEvent.values[0])
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }


    fun start() {
        distanceFromOrigin = 0f
        timeStartMilliseconds = System.currentTimeMillis()
        sensorManager.registerListener(
            sensorEventListener,
            gestureSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }


    fun stop() {
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun onTick(sensorReadingX: Float) {
        val oldDistance = distanceFromOrigin;
        updateDistanceFromOrigin(sensorReadingX)
        val newDistance = distanceFromOrigin;
        updateEventHandlers(oldDistance, newDistance)

        onUpdate()
    }

    private fun updateEventHandlers(oldDistance: Float, newDistance: Float) {
        val previousUpdateDistanceThreshold = floor(oldDistance / sensitivity) * sensitivity;
        val nextUpdateDistanceThreshold = (floor(oldDistance / sensitivity) + 1) * sensitivity;
        if (newDistance < previousUpdateDistanceThreshold) {
            onPrevious()
            currentIndex -= 1
        } else if (newDistance > nextUpdateDistanceThreshold) {
            onNext()
            currentIndex += 1
        }

    }

    private fun updateDistanceFromOrigin(horizontalVelocityMetersPerSecond: Float) {
        val currentTickMilliseconds = getTickMilliseconds()
        val timeDeltaMilliseconds = currentTickMilliseconds - previousTickMilliseconds
        val timeDeltaSeconds = timeDeltaMilliseconds / 1000f
        val distanceMeters = horizontalVelocityMetersPerSecond * timeDeltaSeconds

        distanceFromOrigin += distanceMeters

        previousTickMilliseconds = currentTickMilliseconds
    }

    private fun getTickMilliseconds(): Long {
        val currMilli = System.currentTimeMillis()
        return currMilli - timeStartMilliseconds;
    }
}