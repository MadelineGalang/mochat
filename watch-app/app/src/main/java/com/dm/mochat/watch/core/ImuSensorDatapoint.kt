package com.dm.mochat.watch.core


data class ImuSensorDatapoint(val timestamp: Long, val x: Float, val y: Float, val z: Float){
    fun toCsvString(): String {
        return "$timestamp,$x,$y,$z"
    }

}