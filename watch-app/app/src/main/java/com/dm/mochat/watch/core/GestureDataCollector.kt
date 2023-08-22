package com.dm.mochat.watch.core

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager



class GestureData(
    val accelerometerData: Array<ImuSensorDatapoint>,
    val gyroscopeData: Array<ImuSensorDatapoint>,
    val linearData: Array<ImuSensorDatapoint>
){}

class GestureDataCollector(private val sensorManager: SensorManager) {
    private var timeStartedMilliseconds = 0L;
    private val datapointCount = 160;
    private val _accelerometerDataPoints = Array(datapointCount){ ImuSensorDatapoint(0L,0f, 0f, 0f) }
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            val currentTime = getCurrentTimeElapsed()
            val currentIndex = (currentTime / 10).toInt()
            if(currentIndex >= _accelerometerDataPoints.size){ return }
            _accelerometerDataPoints[currentIndex] = ImuSensorDatapoint(currentTime, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2])
        }
        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    private val _linearDataPoints = Array(datapointCount){ ImuSensorDatapoint(0L,0f, 0f, 0f) }
    private val linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val linearListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            val currentTime = getCurrentTimeElapsed()
            val currentIndex = (currentTime / 10).toInt()
            if(currentIndex >= _linearDataPoints.size){ return }
            _linearDataPoints[currentIndex] = ImuSensorDatapoint(currentTime, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2])
        }
        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    private val _gyroscopeDataPoints = Array(datapointCount){ ImuSensorDatapoint(0L, 0f, 0f, 0f) }
    private val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val gyroscopeListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            val currentTime = getCurrentTimeElapsed()
            val currentIndex = (currentTime / 10).toInt()
            if(currentIndex >= _gyroscopeDataPoints.size){ return }
            _gyroscopeDataPoints[currentIndex] = ImuSensorDatapoint(currentTime, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2])
        }
        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    fun start(){
        timeStartedMilliseconds = System.currentTimeMillis();
        sensorManager.registerListener(linearListener, linearSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun stop(){
        // reset data points
        _linearDataPoints.forEachIndexed { index, _ -> _linearDataPoints[index] = ImuSensorDatapoint(0L, 0f, 0f, 0f) }
        _gyroscopeDataPoints.forEachIndexed { index, _ -> _gyroscopeDataPoints[index] = ImuSensorDatapoint(0L, 0f, 0f, 0f) }
        _accelerometerDataPoints.forEachIndexed { index, _ -> _accelerometerDataPoints[index] = ImuSensorDatapoint(0L, 0f, 0f, 0f) }
        sensorManager.unregisterListener(accelerometerListener)
        sensorManager.unregisterListener(linearListener)
        sensorManager.unregisterListener(gyroscopeListener)
    }

    fun getGestureData(): GestureData{
        return GestureData(
            _accelerometerDataPoints,
            _gyroscopeDataPoints,
            _linearDataPoints
        )
    }

    private fun getCurrentTimeElapsed(): Long{
        return System.currentTimeMillis() - timeStartedMilliseconds
    }
}