package com.dm.mochat.watch.presentation.views.chat

import android.content.Context
import android.hardware.SensorManager
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.dm.mochat.watch.core.DelimiterGestureDetector
import com.dm.mochat.watch.core.Gesture
import com.dm.mochat.watch.core.GestureDataCollector
import com.dm.mochat.watch.helper.TextToSpeechFactory
import com.dm.mochat.watch.helper.TtsHelper.speakThenDo
import com.dm.mochat.watch.helper.Vibration
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import java.util.Locale

@Composable
fun ConfirmMessageScreen(chatViewModel: ChatViewModel = viewModel()) {
    var progress by remember { mutableStateOf(1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val sensorManager =
        LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val tts = TextToSpeechFactory.instance
    lateinit var countDownTimer: CountDownTimer
    val delimiterGestureDetector = DelimiterGestureDetector(sensorManager) {
        it.stop()
        countDownTimer.cancel()
        chatViewModel.sendMessage()
        vibrator.vibrate(Vibration.endGestureVibration)
        val text = "Message sent"
        tts.speakThenDo(text, TextToSpeech.QUEUE_FLUSH, null, "Send message"){
            AppRouter.navigateTo(Screen.HomeScreen)
        }
    }
    countDownTimer = object : CountDownTimer(1500, 10) {
        override fun onTick(millisRemaining: Long) {
            progress = millisRemaining.toFloat() / 1500
        }

        override fun onFinish() {
            delimiterGestureDetector.stop()
            val text = "Message canceled"
            tts.speakThenDo(text, TextToSpeech.QUEUE_FLUSH, null, "Send message"){
                AppRouter.navigateTo(Screen.HomeScreen)
            }
        }
    }



    LaunchedEffect(null) {
        var message = Screen.Data.map["message"]!!
        chatViewModel.updateMessage(message)
        val text = "Do you want to say \"${message}\" to ${chatViewModel.recipient.value}?"
        tts.speakThenDo(text, TextToSpeech.QUEUE_FLUSH, null, "Send message"){
            vibrator.vibrate(Vibration.startGestureVibration)
            delimiterGestureDetector.start()
            countDownTimer.start()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            delimiterGestureDetector.stop()
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
                text = "Do you want to say ${chatViewModel.message.value} to ${chatViewModel.recipient.value}?",
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
