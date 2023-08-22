package com.dm.mochat.watch.presentation.views.home

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import com.dm.mochat.watch.core.Gesture
import com.dm.mochat.watch.presentation.components.ButtonComponent
import com.dm.mochat.watch.presentation.components.GestureNavigableView
import com.dm.mochat.watch.presentation.components.IconButtonComponent
import com.dm.mochat.watch.presentation.components.LargeTextComponent
import com.dm.mochat.watch.presentation.components.NormalTextComponent
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.theme.BlackPearl
import com.dm.mochat.watch.presentation.theme.LightSkyBlue
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    homeViewModel.getCurrentUser()
    homeViewModel.getMessages()

    val messages: List<Map<String, Any>> by homeViewModel.messages.observeAsState(
        initial = emptyList<Map<String, Any>>().toMutableList()
    )
    lateinit var tts: TextToSpeech;
    tts = TextToSpeech(LocalContext.current) {
        if (it == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }
    val scope = rememberCoroutineScope()
    val itemCountBeforeList = 3
    val itemCountAfterList = 1

    val scalingLazyListState = rememberScalingLazyListState()
    GestureNavigableView(onGestureDetected = { gesture, viewModel ->
        when (gesture) {
            Gesture.Left -> {
                // previous message
                Log.d("Gesture Navigation", "Previous message")
                val firstItemIndex = itemCountBeforeList - 1
                val previousIndex = max(scalingLazyListState.centerItemIndex - 1, firstItemIndex)
                scope.launch {
                    scalingLazyListState.animateScrollToItem(previousIndex)
                }

                ttsCurrentMessage(
                    previousIndex,
                    itemCountBeforeList,
                    itemCountAfterList,
                    messages,
                    tts
                )
            }

            Gesture.Right -> {
                // next message
                val lastItemIndex = itemCountBeforeList + messages.size - itemCountAfterList - 1
                val nextIndex = min(scalingLazyListState.centerItemIndex + 1, lastItemIndex)
                Log.d(
                    "Gesture Navigation",
                    "Next message ${scalingLazyListState.layoutInfo.totalItemsCount} $lastItemIndex $nextIndex"
                )

                scope.launch {
                    scalingLazyListState.animateScrollToItem(nextIndex)
                }

                ttsCurrentMessage(nextIndex, itemCountBeforeList, itemCountAfterList, messages, tts)
            }

            Gesture.Up -> {

            }

            Gesture.Down -> {
                // back to conversations
                viewModel.stopGestureDetection()
                AppRouter.navigateTo(Screen.HomeScreen)
                Log.d("Gesture Navigation", "back to conversations")
            }

            Gesture.CircleIn -> {
                // repeat current message
                ttsCurrentMessage(
                    scalingLazyListState.centerItemIndex,
                    itemCountBeforeList,
                    itemCountAfterList,
                    messages,
                    tts
                )
                Log.d("Gesture Navigation", "repeat current message")
            }

            Gesture.CircleOut -> {
                Log.d("Gesture Navigation", "CircleOut")
                viewModel.stopGestureDetection()
                tts.speak("Navigating to contacts", TextToSpeech.QUEUE_FLUSH, null, null)
                val currentIndex = scalingLazyListState.centerItemIndex
                val messageIndex =
                    max(min(0, currentIndex - itemCountBeforeList + 1), messages.size - 1)
                val currentMessage = messages[messageIndex]
                val email =
                    (currentMessage["sent_by"] as? Map<*, *>)?.get("name")?.toString() ?: "Unknown"
                val name =
                    (currentMessage["sent_by"] as? Map<*, *>)?.get("email")?.toString() ?: "Unknown"
                AppRouter.navigateTo(
                    Screen.MessageGestureScreen, mapOf(
                        "email" to email,
                        "name" to name
                    )
                )
            }

            Gesture.Unknown -> {
                // Unknown
                Log.d("Gesture Navigation", "Unknown")
            }
        }
    }) {
        Scaffold(
            timeText = { TimeText() },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) }
        ) {
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = scalingLazyListState
            ) {

                item {
                    IconButtonComponent(
                        iconVector = Icons.Filled.AddComment,
                        description = "New Chat",
                        onButtonClick = { AppRouter.navigateTo(Screen.RecipientScreen) }
                    )

                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LargeTextComponent(
                            text = "Logged in as ${homeViewModel.currentUserName.value}"
                        )
                        NormalTextComponent(text = homeViewModel.currentUserEmail.value!!)
                    }
                }


                items(messages) { m ->
                    val sender = (m["sent_by"] as? Map<*, *>)?.get("name")?.toString() ?: "Unknown"
                    val message = m["message"].toString()
                    val date = m["sent_on"].toString()

                    TitleCard(
                        onClick = {},
                        title = { Text(sender) },
                        time = { Text(text = date, style = TextStyle(fontSize = 10.sp)) },
                    ) {
                        Text(message)
                    }
                }

                item {
                    ButtonComponent(
                        text = "LOG OUT",
                        onButtonClick = {
                            homeViewModel.logout()
                        },
                        textColor = BlackPearl,
                        buttonColor = LightSkyBlue
                    )
                }
            }
        }
    }
}


private fun ttsCurrentMessage(
    currentIndex: Int,
    itemCountBeforeList: Int,
    itemCountAfterList: Int,
    messages: List<Map<String, Any>>,
    tts: TextToSpeech,
) {
    val currentMessage = messages[currentIndex - itemCountBeforeList + 1]
    val sender = (currentMessage["sent_by"] as? Map<*, *>)?.get("name")?.toString() ?: "Unknown"
    val message = currentMessage["message"].toString()
    val date = currentMessage["sent_on"].toString()
    var ttsMessage = "$sender said $message on $date"
    if (currentIndex == itemCountBeforeList - 1) {
        ttsMessage = "Latest message. $ttsMessage"
    } else if (currentIndex == itemCountBeforeList + messages.size - itemCountAfterList - 1) {
        ttsMessage = "Oldest message. $ttsMessage"
    }

    tts.speak(ttsMessage, TextToSpeech.QUEUE_FLUSH, null, null)
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}