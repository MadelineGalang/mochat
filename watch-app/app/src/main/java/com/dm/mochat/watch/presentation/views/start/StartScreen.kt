package com.dm.mochat.watch.presentation.views.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import com.dm.mochat.watch.presentation.components.ButtonComponent
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.theme.BlackPearl
import com.dm.mochat.watch.presentation.theme.LightCyan
import com.dm.mochat.watch.presentation.theme.LightSkyBlue

@Composable
fun StartScreen() {
    Scaffold(
        timeText = { TimeText() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonComponent(
                text = "LOG IN",
                onButtonClick = { AppRouter.navigateTo(Screen.LoginScreen) },
                textColor = BlackPearl,
                buttonColor = LightSkyBlue
            )
            Spacer(modifier = Modifier.height(10.dp))
            ButtonComponent(
                text = "REGISTER",
                onButtonClick = { AppRouter.navigateTo(Screen.RegisterScreen) },
                textColor = LightCyan,
                buttonColor = BlackPearl
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun StartScreenPreview() {
    StartScreen()
}