package com.dm.mochat.watch.presentation.views.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.dm.mochat.watch.presentation.components.ButtonComponent
import com.dm.mochat.watch.presentation.components.IconButtonComponent
import com.dm.mochat.watch.presentation.components.LargeTextComponent
import com.dm.mochat.watch.presentation.components.NormalTextComponent
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.theme.BlackPearl
import com.dm.mochat.watch.presentation.theme.LightSkyBlue

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    homeViewModel.getCurrentUser()
    homeViewModel.getMessages()

    val messages: List<Map<String, Any>> by homeViewModel.messages.observeAsState(
        initial = emptyList<Map<String, Any>>().toMutableList()
    )

    val scalingLazyListState = rememberScalingLazyListState()

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

            items(messages) {m ->
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

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}