package com.speakbyhand.datacollectorclient.core

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.dm.mochat.watch.core.ShakeDetector

object DelimiterGestureDetector{
    private var sensorManager: SensorManager? = null
    private var onDelimiterDetected: () -> Unit = {}
    private var linearSensor: Sensor? = null

    fun configure(sensorManager: SensorManager, onDelimiterDetected: () -> Unit){
        this.sensorManager = sensorManager
        this.onDelimiterDetected = onDelimiterDetected
        linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    }
    var started = false
    private val shakeDetector: ShakeDetector = ShakeDetector(15, 80f, 50)
    private val eventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            shakeDetector.update(x, y, z)
            if(shakeDetector.isShaking && started){
                onDelimiterDetected()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    fun start(){
        if (started) { return }
        if (sensorManager == null) { throw Exception("SensorManager is not configured") }
        sensorManager!!.registerListener(eventListener, linearSensor, SensorManager.SENSOR_DELAY_FASTEST)
        started = true
    }

    fun stop(){
        if (!started) { return }
        if (sensorManager == null) { throw Exception("SensorManager is not configured") }
        sensorManager!!.unregisterListener(eventListener, linearSensor)
        shakeDetector.reset()
        started = false
    }

    fun isDelimiterDetected(): Boolean {
        return shakeDetector.isShaking
    }
}