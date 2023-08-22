package com.dm.mochat.watch.presentation.components

import android.content.Context.SENSOR_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.hardware.SensorManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.dm.mochat.watch.core.Gesture

@Composable
fun GestureNavigableView(
    onGestureDetected: (Gesture, GestureNavigableViewModel) -> Unit,
    child: @Composable () -> Unit
) {
    val vibrator = LocalContext.current.getSystemService(VIBRATOR_SERVICE) as Vibrator
    val sensorManager = LocalContext.current.getSystemService(SENSOR_SERVICE) as SensorManager
    LaunchedEffect(Unit) {
        GestureNavigableViewModel.configure(
            sensorManager,
            onDelimiterDetected = {
                Log.d("Gesture Navigation", "Detected delimiter")
                vibrator.vibrate(VibrationEffect.createOneShot(150, 200))
            },
            onNavigationGestureDetected = { gesture, viewModel ->
                Log.d("Gesture Navigation", "Detected Navigation Gesture")
                vibrator.vibrate(VibrationEffect.createOneShot(150, 200))
                onGestureDetected(gesture, viewModel)
            })
        GestureNavigableViewModel.startGestureDetection()
    }

    DisposableEffect(Unit) {
        onDispose {
            GestureNavigableViewModel.stopGestureDetection()
        }
    }

    child()
}