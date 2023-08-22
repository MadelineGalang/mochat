package com.dm.mochat.watch.presentation.components

import android.hardware.SensorManager
import android.os.CountDownTimer
import android.util.Log
import com.dm.mochat.watch.core.ApiNavigationGestureDetector
import com.dm.mochat.watch.core.DelimiterGestureDetector
import com.dm.mochat.watch.core.Gesture
import com.dm.mochat.watch.core.GestureDataCollector

object GestureNavigableViewModel {
    private var sensorManager: SensorManager? = null
    private var onDelimiterDetected: () -> Unit = {}
    private var onNavigationGestureDetected: (gesture: Gesture, viewModel: GestureNavigableViewModel) -> Unit =
        { _, _ -> }
    private var delimiterGestureDetector: DelimiterGestureDetector? = null
    private var navigationGestureDataCollector: GestureDataCollector? = null
    private var navigationGestureDataPredictor = ApiNavigationGestureDetector()
    private val timerDuration = 1600L

    fun configure(
        sensorManager: SensorManager,
        onDelimiterDetected: () -> Unit,
        onNavigationGestureDetected: (gesture: Gesture, viewModel: GestureNavigableViewModel) -> Unit
    ) {
        stopGestureDetection()
        this.sensorManager = sensorManager
        this.onDelimiterDetected = onDelimiterDetected
        this.onNavigationGestureDetected = onNavigationGestureDetected
        this.delimiterGestureDetector =
            DelimiterGestureDetector(sensorManager, ::_onDelimiterDetected)
        this.navigationGestureDataCollector = GestureDataCollector(sensorManager)
    }


    public var started = false
        private set

    fun startGestureDetection() {
        if (started) {
            Log.i(
                "Gesture Navigation",
                "Warning: Called start gesture detection when it's already started"
            ); return
        }
        Log.d("Gesture Navigation", "Staring gesture detection")
        delimiterGestureDetector!!.start()
        started = true
        started = true
    }

    fun stopGestureDetection() {
        if (!started) {
            Log.i(
                "Gesture Navigation",
                "Warning: Called stop gesture detection when it's already stopped"
            ); return
        }
        delimiterGestureDetector!!.stop()
        navigationGestureDataCollector!!.stop()
        started = false
    }

    private fun _onDelimiterDetected(context: DelimiterGestureDetector) {
        onDelimiterDetected()
        delimiterGestureDetector!!.stop()
        navigationGestureDataCollector!!.start()
        val countDownTimer = object : CountDownTimer(timerDuration, 10) {
            override fun onTick(millisRemaining: Long) {
            }
            override fun onFinish() {
                navigationGestureDataCollector!!.stop()
                val gestureData = navigationGestureDataCollector!!.getGestureData()
                navigationGestureDataCollector!!.reset()
                navigationGestureDataPredictor.predictGesture(gestureData) {
                    _onNavigationGestureDetected(it);
                }
            }
        }.start()

    }

    private fun _onNavigationGestureDetected(gesture: Gesture) {
        navigationGestureDataCollector!!.stop()
        delimiterGestureDetector!!.start()
        onNavigationGestureDetected(gesture, this)
    }
}