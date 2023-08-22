package com.speakbyhand.datacollectorclient.core;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class GestureDataRecorder {
    private final SensorManager sensorManager;
    private final Sensor gestureSensor;
    private final int AVERAGE_DATA_POINTS_COUNT = 250;
    private long startMilli;
    private final ArrayList<ImuSensorReading> gestureDataPoints = new ArrayList<>(AVERAGE_DATA_POINTS_COUNT);

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            long timeMiliseconds = getTimeMilliseconds();
            gestureDataPoints.add(new ImuSensorReading(timeMiliseconds, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
            getTimeMilliseconds();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    public GestureDataRecorder(Context onCreateContext, String sensorService, int sensorType) {
        sensorManager = (SensorManager) onCreateContext.getSystemService(sensorService);
        gestureSensor = sensorManager.getDefaultSensor(sensorType);
    }

    public void start() {
        startMilli = System.currentTimeMillis();
        sensorManager.registerListener(sensorEventListener, gestureSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    public void reset() {
        gestureDataPoints.clear();
    }

    private long getTimeMilliseconds() {
        long currMilli = System.nanoTime();
        return currMilli - startMilli;
    }

    public String getDataAsCsvString() {
        StringBuilder csvContentBuilder = new StringBuilder();
        // BUG: accDataPoints and gyroDataPoints are not the same size sometimes (Wont Fix)
        for (int i = 0; i < Math.min(gestureDataPoints.size(), gestureDataPoints.size()); i++) {
            ImuSensorReading gestureSample = gestureDataPoints.get(i);

            // Join x,y,z values of acc and gyro data samples by comma
            csvContentBuilder.append(String.join(",",
                    String.valueOf(gestureSample.time),
                    String.valueOf(gestureSample.x),
                    String.valueOf(gestureSample.y),
                    String.valueOf(gestureSample.z)
            ));
            csvContentBuilder.append("\n");
        }
        return csvContentBuilder.toString();
    }
}