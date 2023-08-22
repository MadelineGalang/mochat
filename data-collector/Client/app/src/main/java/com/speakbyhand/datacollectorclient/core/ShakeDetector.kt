package com.dm.mochat.watch.core

import android.util.Log
import com.speakbyhand.app.core.LimitedDeque

class ShakeDetector(
    private val shakeThreshold: Int,
    private val accelerationThreshold: Float,
    private val windowSize: Int
) {
    private val stack: LimitedDeque<Boolean> = LimitedDeque(windowSize)
    var acceleratingCount = 0

    fun update(x: Float, y: Float, z: Float) {
        val accelerating = isAccelerating(x, y, z)
        // log the bottom of the stack
        if (stack.isFull && stack.bottom() == true) {
            acceleratingCount -= 1
        }
        if (accelerating) {
            acceleratingCount += 1
        }
        stack.push(accelerating)
    }

    fun reset() {
        stack.clear()
        acceleratingCount = 0
    }

    fun isAccelerating(x: Float, y: Float, z: Float): Boolean {
        val magnitudeSquared = (x * x + y * y + z * z).toDouble()
        return magnitudeSquared > accelerationThreshold
    }

    val isShaking: Boolean
        get() = acceleratingCount >= shakeThreshold
}
