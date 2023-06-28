/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.speakbyhand.datacollectorclient.presentation

import android.hardware.Sensor
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.speakbyhand.datacollectorclient.core.GestureDataApiService
import com.speakbyhand.datacollectorclient.core.GestureDataRecorder
import com.speakbyhand.datacollectorclient.presentation.theme.DataCollectorClientTheme
import java.util.*


class MainActivity : ComponentActivity() {
    lateinit var apiService: GestureDataApiService
    lateinit var accelDataRecorder: GestureDataRecorder
    lateinit var gyroDataRecorder: GestureDataRecorder
    lateinit var linearDataRecorder: GestureDataRecorder


    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        apiService = GestureDataApiService()
        accelDataRecorder = GestureDataRecorder(this, SENSOR_SERVICE, Sensor.TYPE_ACCELEROMETER)
        gyroDataRecorder = GestureDataRecorder(this, SENSOR_SERVICE, Sensor.TYPE_GYROSCOPE)
        linearDataRecorder =
            GestureDataRecorder(this, SENSOR_SERVICE, Sensor.TYPE_LINEAR_ACCELERATION)


        super.onCreate(savedInstanceState)
        setContent {
            WearApp({ startRecording() }, { gestureName -> stopRecording(gestureName) })
        }
    }

    fun startRecording() {
        accelDataRecorder.start()
        gyroDataRecorder.start()
        linearDataRecorder.start()
    }

    fun stopRecording(gestureName: String) {
        accelDataRecorder.stop()
        gyroDataRecorder.stop()
        linearDataRecorder.stop()
        playSpeech()

        val mergedAccelData: String = accelDataRecorder.getDataAsCsvString()
        val mergedGyroData: String = gyroDataRecorder.getDataAsCsvString()
        val mergedLinearData: String = linearDataRecorder.getDataAsCsvString()

        apiService.postGestureData(gestureName, mergedAccelData, mergedGyroData, mergedLinearData)
        gyroDataRecorder.reset()
        accelDataRecorder.reset()
        linearDataRecorder.reset()
    }

    fun playSpeech() {
        tts = TextToSpeech(applicationContext) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                tts.setSpeechRate(1.0f)
                tts.speak("Recording Successful", TextToSpeech.QUEUE_ADD, null)
            }
        }
    }
}


@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun WearApp(onStart: (String) -> Unit, onStop: (String) -> Unit) {


    DataCollectorClientTheme {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "message_list"
        ) {
            composable("message_list") {
                selectGesture(onGestureSelected = { gestureName ->
                    navController.navigate("record/$gestureName")
                })
            }
            composable("record/{gestureName}") {
                val gestureName = it.arguments?.getString("gestureName")!!
                recordingView(gestureName, onStart, onStop)
            }
        }

    }
}

@Composable
private fun selectGesture(onGestureSelected: (String) -> Unit) {
    val items = listOf("not_pinched", "pinched", "to_pinched", "release_pinched")
    val state = rememberPickerState(items.size)
    val contentDescription by remember { derivedStateOf { "${state.selectedOption + 1}" } }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selected: ${items[state.selectedOption]}"
        )
        Picker(
            modifier = Modifier.size(120.dp, 70.dp),
            state = state,
        ) {
            Box(
                Modifier
                    .padding(vertical = 5.dp)
                    .background(MaterialTheme.colors.primary, shape = RoundedCornerShape(10.dp))
                    .width(150.dp),
                ) {
                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                    color = MaterialTheme.colors.onBackground,
                    text = items[it]
                )
            }
        }
        Button(
            onClick = {
                onGestureSelected(items[state.selectedOption])
            },
        ) {
            Text(text = "Select")
        }
    }
}

@Composable
private fun recordingView(
    gestureName: String,
    onStart: (String) -> Unit,
    onStop: (String) -> Unit,
) {
    val IDLE_STATE = "IDLE"
    val RECORDING_STATE = "RECORDING"
    val currentState = remember { mutableStateOf(IDLE_STATE) }
    var nums: Long by remember { mutableStateOf(3) }
    var setView: String by remember { mutableStateOf("Click to Start...") }

    val timer = object : CountDownTimer(3000, 10) {
        override fun onTick(millisUntilFinished: Long) {
            setView = "$nums"
            nums = millisUntilFinished / 1000
        }

        override fun onFinish() {
            setView = "Click to Start..."
            onStop(gestureName)
            currentState.value = IDLE_STATE
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(
            text = "Gesture: $gestureName"
        )
        Button(
            enabled = currentState.value == IDLE_STATE,
            onClick = {
                onStart(gestureName)
                timer.start()
                currentState.value = RECORDING_STATE
            },
        ) {
            Text(text = "Start")
        }
        Text(
            text = "$setView"
        )
    }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    selectGesture({})
}