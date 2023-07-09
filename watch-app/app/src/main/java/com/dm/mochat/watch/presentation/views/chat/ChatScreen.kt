package com.dm.mochat.watch.presentation.views.chat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.ProgressIndicatorDefaults
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.dm.mochat.watch.presentation.components.IconButtonComponent
import com.dm.mochat.watch.presentation.components.LargeTextComponent
import com.dm.mochat.watch.presentation.components.TextFieldWithValueComponent
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.navigation.SystemBackButtonHandler
import com.dm.mochat.watch.presentation.theme.NavyBlue

@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel()) {
    val message by chatViewModel.message.observeAsState(initial = "")

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val scalingLazyListState = rememberScalingLazyListState()

    val isDetecting by chatViewModel.isDetecting.observeAsState(initial = false)
    val progress by chatViewModel.timerProgress.observeAsState(1.0F)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    SwipeToDismissBox(
        onDismissed = { AppRouter.navigateTo(Screen.RecipientScreen) },
        state = swipeToDismissBoxState
    ) { isBackground ->
        if (isBackground) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background))
        } else {
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
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = scalingLazyListState
                ) {
                    item {
                        LargeTextComponent(
                            text = chatViewModel.recipient.value ?: ""
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextFieldWithValueComponent(
                                value = message,
                                placeholder = "Message",
                                onTextChange = {
                                    chatViewModel.updateMessage(it)
                                },
                                singleLine = false
                            )
                            Text(
                                text = "Perform gesture now",
                                style = TextStyle(fontSize = 10.sp),
                                color = NavyBlue,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    item {
                        IconButtonComponent(
                            iconVector = Icons.Filled.Send,
                            description = "Send message",
                            onButtonClick = {
                                chatViewModel.sendMessage()
                            }
                        )
                    }

                    item {
                        CompactChip(
                            onClick = { chatViewModel.simulateGesture() },
                            label = {
                                Text(text = "Simulate gesture")
                            },
                            colors = ChipDefaults.secondaryChipColors()
                        )
                    }
                }

                if (isDetecting) {
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
        }
    }

    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.RecipientScreen)
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}