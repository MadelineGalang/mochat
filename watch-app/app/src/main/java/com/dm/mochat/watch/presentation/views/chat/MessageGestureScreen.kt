package com.dm.mochat.watch.presentation.views.chat

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProgressIndicatorDefaults
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.dm.mochat.watch.core.ApiMessageGestureDetector
import com.dm.mochat.watch.core.ApiNavigationGestureDetector
import com.dm.mochat.watch.core.Gesture
import com.dm.mochat.watch.core.GestureDataCollector
import com.dm.mochat.watch.helper.TextToSpeechFactory
import com.dm.mochat.watch.helper.TtsHelper.speakThenDo
import com.dm.mochat.watch.helper.Vibration
import com.dm.mochat.watch.presentation.components.GestureNavigableViewModel
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MessageGestureScreen(chatViewModel: ChatViewModel = viewModel()) {
    var progress by remember { mutableStateOf(1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    val sensorManager = LocalContext.current.getSystemService(SENSOR_SERVICE) as SensorManager
    val messageGestureDataCollector = GestureDataCollector(sensorManager)
    val messageGestureDataPredictor = ApiNavigationGestureDetector()
    val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val tts = TextToSpeechFactory.instance

    LaunchedEffect(null) {
        chatViewModel.selectRecipient(Screen.Data.map["email"]!!, Screen.Data.map["name"]!!)
        val countDownTimer = object : CountDownTimer(1600L, 10) {
            override fun onTick(millisRemaining: Long) {
                progress = millisRemaining.toFloat() / 1600L
            }

            override fun onFinish() {
                messageGestureDataCollector.stop()
                vibrator.vibrate(Vibration.endGestureVibration)
                val gestureData = messageGestureDataCollector.getGestureData()
                messageGestureDataCollector.reset()
                messageGestureDataPredictor.predictGesture(gestureData) { gesture: Gesture ->
                    GlobalScope.launch {
                        chatViewModel.updateMessage(chatViewModel.getMessageFromGesture(gesture))
                    }
                    AppRouter.navigateTo(Screen.ConfirmMessageScreen, mapOf("message" to chatViewModel.getMessageFromGesture(gesture)))
                }

            }
        }
        tts.speakThenDo("Perform message gesture", TextToSpeech.QUEUE_FLUSH, null, "Perform gesture") {
            vibrator.vibrate(Vibration.startGestureVibration)
            messageGestureDataCollector.start()
            countDownTimer.start()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            messageGestureDataCollector.stop()
        }
    }


    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Perform Gesture Now",
                style = TextStyle(fontSize = 16.sp),
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )
        }
        CircularProgressIndicator(
            startAngle = 300f,
            endAngle = 240f,
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 1.dp)
        )
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun MessageGestureScreenPreview() {
    MessageGestureScreen()
}