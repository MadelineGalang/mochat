package com.dm.mochat.watch.presentation.views.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.dm.mochat.watch.presentation.components.ChipComponent
import com.dm.mochat.watch.presentation.components.LargeTextComponent
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.navigation.SystemBackButtonHandler
import com.dm.mochat.watch.presentation.theme.LightPowderBlue
import com.dm.mochat.watch.presentation.theme.NavyBlue

@Composable
fun RecipientScreen(chatViewModel: ChatViewModel = viewModel()) {
    chatViewModel.getRecipients()

    val recipients: List<Map<String, Any>> by chatViewModel.recipients.observeAsState(
        initial = emptyList<Map<String, Any>>().toMutableList()
    )

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val scalingLazyListState = rememberScalingLazyListState()

    SwipeToDismissBox(
        onDismissed = { AppRouter.navigateTo(Screen.HomeScreen) },
        state = swipeToDismissBoxState
    ) { isBackground ->
        if (isBackground) {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background))
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
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp,
                        bottom = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = scalingLazyListState
                ) {
                    item {
                        LargeTextComponent(
                            text = "Select Recipient",
                            bold = true
                        )
                    }

                    items(recipients) { r ->
                        val name = r["name"].toString()
                        val email = r["email"].toString()

                        ChipComponent(
                            text = name,
                            onChipClick = {
                                chatViewModel.selectRecipient(email, name)
                                AppRouter.navigateTo(Screen.ChatScreen)
                            },
                            textColor = LightPowderBlue,
                            chipColor = NavyBlue
                        )
                    }
                }
            }
        }
    }

    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.HomeScreen)
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun RecipientScreenPreview() {
    RecipientScreen()
}