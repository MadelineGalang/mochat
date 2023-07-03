package com.dm.mochat.watch.presentation.views.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import com.dm.mochat.watch.presentation.components.LargeTextComponent
import com.dm.mochat.watch.presentation.components.IconButtonComponent
import com.dm.mochat.watch.presentation.components.TextFieldComponent
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.navigation.SystemBackButtonHandler
import com.dm.mochat.watch.presentation.theme.LightCyan

@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel = viewModel()) {
    val scalingLazyListState = rememberScalingLazyListState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if(registerViewModel.loading.value == true) {
            CircularProgressIndicator()
        }

        Scaffold(
            timeText = { TimeText() },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            positionIndicator = { PositionIndicator(scalingLazyListState = scalingLazyListState) }
        ) {
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = scalingLazyListState
            ) {
                item{ LargeTextComponent(text = "Register", color = LightCyan) }

                item {
                    TextFieldComponent(placeholder = "Name", onTextChange = {
                        registerViewModel.updateName(it)
                    })
                }

                item {
                    TextFieldComponent(placeholder = "Email", onTextChange = {
                        registerViewModel.updateEmail(it)
                    })
                }

                item {
                    TextFieldComponent(placeholder = "Password", onTextChange = {
                        registerViewModel.updatePassword(it)
                    }, isPassword = true)
                }

                item {
                    IconButtonComponent(
                        iconVector = Icons.Filled.Check,
                        description = "Register",
                        onButtonClick = {
                            registerViewModel.register()
                        },
                    )
                }
            }
        }

    }

    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.StartScreen)
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}