/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.speakbyhand.datacollectorclient.presentation

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.speakbyhand.datacollectorclient.core.DelimiterGestureDetector
import com.speakbyhand.datacollectorclient.core.GestureDataApiService
import com.speakbyhand.datacollectorclient.core.GestureDataRecorder
import com.speakbyhand.datacollectorclient.core.GestureSlider
import com.speakbyhand.datacollectorclient.presentation.theme.DataCollectorClientTheme
import java.util.*


class MainActivity : ComponentActivity() {
    lateinit var apiService: GestureDataApiService
    lateinit var accelDataRecorder: GestureDataRecorder
    lateinit var gyroDataRecorder: GestureDataRecorder
    lateinit var linearDataRecorder: GestureDataRecorder
    lateinit var gestureSlider: GestureSlider
    lateinit var vibrator: Vibrator


    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        apiService = GestureDataApiService()
        accelDataRecorder = GestureDataRecorder(this, SENSOR_SERVICE, Sensor.TYPE_ACCELEROMETER)
        gyroDataRecorder = GestureDataRecorder(this, SENSOR_SERVICE, Sensor.TYPE_GYROSCOPE)
        linearDataRecorder =
            GestureDataRecorder(this, SENSOR_SERVICE, Sensor.TYPE_LINEAR_ACCELERATION)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val sensorManager = this.getSystemService(SENSOR_SERVICE) as SensorManager
        gestureSlider = GestureSlider(
            sensorManager,
            Sensor.TYPE_LINEAR_ACCELERATION,
            onNext = { },
            onPrevious = { },
            onUpdate = { },
            sensitivity = 10f
        )


        super.onCreate(savedInstanceState)
        setContent {
            WearApp(
                { startRecording() },
                { gestureName -> stopRecording(gestureName) },
                { startTesting() },
                { callback -> stopTesting(callback) },
                gestureSlider,
                vibrator
            )
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

        apiService.postGestureDataRecording(
            gestureName,
            mergedAccelData,
            mergedGyroData,
            mergedLinearData
        )
        gyroDataRecorder.reset()
        accelDataRecorder.reset()
        linearDataRecorder.reset()
    }

    fun startTesting() {
        accelDataRecorder.start()
        gyroDataRecorder.start()
        linearDataRecorder.start()
    }

    fun stopTesting(callback: (String) -> Unit) {
        accelDataRecorder.stop()
        gyroDataRecorder.stop()
        linearDataRecorder.stop()
        playSpeech()

        val mergedAccelData: String = accelDataRecorder.dataAsCsvString
        val mergedGyroData: String = gyroDataRecorder.dataAsCsvString
        val mergedLinearData: String = linearDataRecorder.dataAsCsvString

        apiService.getGestureDataPrediction(
            mergedAccelData,
            mergedGyroData,
            mergedLinearData,
            callback
        )
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
fun WearApp(
    onRecordStart: (String) -> Unit,
    onRecordStop: (String) -> Unit,
    onTestStart: () -> Unit,
    onTestStop: ((String) -> Unit) -> Unit,
    gestureSlider: GestureSlider,
    vibrator: Vibrator
) {
    var recordTimeMilliseconds: Long = 1600
    DataCollectorClientTheme {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = "selectAction"
        ) {
            composable("selectAction") {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        navController.navigate("selectGesture")
                    }) {
                        Text("Capture")
                    }
                    Button(onClick = {
                        navController.navigate("testingView")
                    }) {
                        Text("Test")
                    }
                    Button(onClick = {
                        navController.navigate("slider")
                    }) {
                        Text("Slider")
                    }
                    Button(onClick = {
                        navController.navigate("sensorTest")
                    }) {
                        Text("sensorTest")
                    }
                }
            }
            composable("testingView") {
                testingView(onTestStart, onTestStop, recordTimeMilliseconds)
            }
            composable("selectGesture") {
                selectGesture(onGestureSelected = { gestureName ->
                    navController.navigate("record/$gestureName")
                })
            }
            composable("record/{gestureName}") {
                val gestureName = it.arguments?.getString("gestureName")!!
                recordingView(
                    vibrator,
                    gestureName,
                    onRecordStart,
                    onRecordStop,
                    recordTimeMilliseconds
                )
            }
            composable("slider") {
                gestureSliderView(gestureSlider)
            }
            composable("sensorTest") {
                sensorTestView()
            }
        }
    }
}

@Composable
private fun selectGesture(onGestureSelected: (String) -> Unit) {
    val items = listOf(
        "test",
        "up",
        "down",
        "left",
        "right",
        "circle_in",
        "circle_out",
        "delimiter",
        "nondelimiter"
    )
    val state = rememberPickerState(items.size)
    val contentDescription by remember { derivedStateOf { "${state.selectedOption + 1}" } }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
    vibrator: Vibrator,
    gestureName: String,
    onStart: (String) -> Unit,
    onStop: (String) -> Unit,
    recordTimeMilliseconds: Long
) {
    val IDLE_STATE = "IDLE"
    val RECORDING_STATE = "RECORDING"
    val count = remember { mutableStateOf(0) }
    val currentState = remember { mutableStateOf(IDLE_STATE) }
    var nums: Long by remember { mutableStateOf(3) }
    var setView: String by remember { mutableStateOf("Click to Start...") }

    val timer = object : CountDownTimer(recordTimeMilliseconds, 10) {
        override fun onTick(millisUntilFinished: Long) {
            setView = "$nums"
            nums = millisUntilFinished / 1000
        }

        override fun onFinish() {
            if (!DelimiterGestureDetector.started) {
                setView = "Click to Start..."
                currentState.value = IDLE_STATE
                vibrator.vibrate(VibrationEffect.createOneShot(150, 200))
                onStop(gestureName)
                DelimiterGestureDetector.start()
                count.value += 1
            }
        }
    }

    val sensorManager =
        LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    DelimiterGestureDetector.configure(sensorManager) {
        if (DelimiterGestureDetector.started) {
            DelimiterGestureDetector.started = false;
            vibrator.vibrate(VibrationEffect.createOneShot(150, 200))
            DelimiterGestureDetector.stop()
            onStart(gestureName)
            timer.start()
            currentState.value = RECORDING_STATE
        }
    }
    if (!DelimiterGestureDetector.started) {
        DelimiterGestureDetector.start()
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
        Text(
            text = "Duration ${recordTimeMilliseconds}ms"
        )

        Button(
            enabled = currentState.value == IDLE_STATE,
            onClick = {
                vibrator.vibrate(VibrationEffect.createOneShot(150, 200))
                onStart(gestureName)
                timer.start()
                currentState.value = RECORDING_STATE
            },
        ) {
            Text(text = "Start")
        }
        Text(
            text = "Count: ${count.value}"
        )
    }
}

@Composable
private fun testingView(
    onStart: () -> Unit,
    onStop: ((String) -> Unit) -> Unit,
    recordTimeMilliseconds: Long
) {
    val IDLE_STATE = "IDLE"
    val RECORDING_STATE = "RECORDING"
    val detectedGesture = remember { mutableStateOf("") }
    val currentState = remember { mutableStateOf(IDLE_STATE) }
    var nums: Long by remember { mutableStateOf(3) }
    var setView: String by remember { mutableStateOf("Click to Start...") }
    val timer = object : CountDownTimer(recordTimeMilliseconds, 10) {
        override fun onTick(millisUntilFinished: Long) {
            setView = "$nums"
            nums = millisUntilFinished / 1000
        }

        override fun onFinish() {
            setView = "Click to Start..."
            onStop { detectedValue: String ->
                detectedGesture.value = detectedValue
            }
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
            text = "Test Gestures"
        )
        Text(
            text = "Duration ${recordTimeMilliseconds}ms"
        )
        Button(
            enabled = currentState.value == IDLE_STATE,
            onClick = {
                onStart()
                timer.start()
                currentState.value = RECORDING_STATE
            },
        ) {
            Text(text = "Start")
        }
        Text(
            text = "Detected: ${detectedGesture.value}"
        )
    }
}


@Composable
private fun gestureSliderView(gestureSlider: GestureSlider) {
    var value by remember { mutableStateOf(4.5f) }
    var index by remember { mutableStateOf(gestureSlider.currentIndex) }
    var distance by remember { mutableStateOf(gestureSlider.distanceFromOrigin) }
    var isStarted by remember { mutableStateOf(false) }
    gestureSlider.onUpdate = {
        index = gestureSlider.currentIndex
        distance = gestureSlider.distanceFromOrigin
    }

    gestureSlider.onNext = {
        index = gestureSlider.currentIndex
    }

    gestureSlider.onPrevious = {
        index = gestureSlider.currentIndex
    }

    Column(Modifier, Arrangement.Center, Alignment.CenterHorizontally) {
        Text(text = "Sensitivity: $value")
        Text(text = "Index: $index")
        Text(text = "Distance: $distance")
        InlineSlider(
            value = value,
            onValueChange = {
                value = it
                gestureSlider.sensitivity = it;
            },
            valueRange = 5f..20f,
            steps = 10,
            segmented = false
        )
        Button(onClick = {
            if (isStarted) {
                gestureSlider.stop()
                isStarted = false
            } else {
                gestureSlider.start()
                isStarted = true
            }
        }) {
            Text(text = if (isStarted) "Stop" else "Start")
        }
    }
}


@Composable
private fun sensorTestView() {
    val sensorManager = LocalContext.current.getSystemService(SENSOR_SERVICE) as SensorManager
    val sensorDelay = SensorManager.SENSOR_DELAY_NORMAL

    val accReading = remember { mutableStateListOf(0.0f, 0.0f, 0.0f) }
    val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val accListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            accReading[0] = sensorEvent.values[0]
            accReading[1] = sensorEvent.values[1]
            accReading[2] = sensorEvent.values[2]
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    val gyroReading = remember { mutableStateListOf(0.0f, 0.0f, 0.0f) }
    val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    val gyroListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            gyroReading[0] = sensorEvent.values[0]
            gyroReading[1] = sensorEvent.values[1]
            gyroReading[2] = sensorEvent.values[2]
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    val linearReading = remember { mutableStateListOf(0.0f, 0.0f, 0.0f) }
    val linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    val linearListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            linearReading[0] = sensorEvent.values[0]
            linearReading[1] = sensorEvent.values[1]
            linearReading[2] = sensorEvent.values[2]
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }


    LaunchedEffect(Unit){
        sensorManager.registerListener(linearListener, linearSensor, sensorDelay)
        sensorManager.registerListener(gyroListener, gyroSensor, sensorDelay)
        sensorManager.registerListener(accListener, accSensor, sensorDelay)
        Log.i("TEST_VIEW", "---Initialized listeners---")
    }

    DisposableEffect(Unit) {
        onDispose {
            sensorManager.unregisterListener(accListener)
            sensorManager.unregisterListener(gyroListener)
            sensorManager.unregisterListener(linearListener)
            Log.i("TEST_VIEW", "---Disposed listeners---")
        }
    }

    Column(Modifier, Arrangement.Center, Alignment.CenterHorizontally) {
        Text(text = "Gyroscope: %.2f, %.2f, %.2f".format(gyroReading[0], gyroReading[1], gyroReading[2]))
        Text(text = "Accelerometer: %.2f, %.2f, %.2f".format(accReading[0], accReading[1], accReading[2]))
        Text(text = "Linear: %.2f, %.2f, %.2f".format(linearReading[0], linearReading[1], linearReading[2]))
    }

}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    testingView({}, { "" }, 1600)
}