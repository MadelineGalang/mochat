package com.speakbyhand.datacollectorclient.core;

class ImuSensorReading {
    public long time;
    public float x;
    public float y;
    public float z;

    public ImuSensorReading(long time, float x, float y, float z) {
        this.time = time;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
