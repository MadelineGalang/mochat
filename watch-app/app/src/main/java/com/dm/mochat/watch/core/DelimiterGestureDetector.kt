package com.dm.mochat.watch.core

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class DelimiterGestureDetector(private val sensorManager: SensorManager, val onDelimiterDetected: (DelimiterGestureDetector) -> Unit) {
    private val shakeDetector: ShakeDetector = ShakeDetector(15, 80f, 50)
    private val linearSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val eventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            shakeDetector.update(x, y, z)
            if(shakeDetector.isShaking){
                onDelimiterDetected(this@DelimiterGestureDetector)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    fun isDetected(): Boolean{
        return shakeDetector.isShaking
    }


    fun start(){
        sensorManager.registerListener(eventListener, linearSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun stop(){
        sensorManager.unregisterListener(eventListener, linearSensor)
        shakeDetector.reset()
    }
}