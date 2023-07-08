package com.dm.mochat.watch.presentation.views.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.dm.mochat.watch.presentation.components.LargeTextComponent
import com.dm.mochat.watch.presentation.components.IconButtonComponent
import com.dm.mochat.watch.presentation.components.TextFieldWithValueComponent
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.navigation.SystemBackButtonHandler
import com.dm.mochat.watch.presentation.theme.LightSkyBlue

@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel = viewModel()) {
    val name: String by registerViewModel.name.observeAsState(initial = "")
    val email: String by registerViewModel.email.observeAsState(initial = "")
    val password: String by registerViewModel.password.observeAsState(initial = "")
    val loading: Boolean by registerViewModel.loading.observeAsState(initial = false)

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val scalingLazyListState = rememberScalingLazyListState()

    SwipeToDismissBox(
        onDismissed = { AppRouter.navigateTo(Screen.StartScreen) },
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
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = scalingLazyListState
                ) {
                    item { LargeTextComponent(text = "Register") }

                    item {
                        TextFieldWithValueComponent(
                            value = name,
                            placeholder = "Name",
                            onTextChange = {
                                registerViewModel.updateName(it)
                            }
                        )
                    }

                    item {
                        TextFieldWithValueComponent(
                            value = email,
                            placeholder = "Email",
                            onTextChange = {
                                registerViewModel.updateEmail(it)
                            }
                        )
                    }

                    item {
                        TextFieldWithValueComponent(
                            value = password,
                            placeholder = "Password",
                            onTextChange = {
                                registerViewModel.updatePassword(it)
                            },
                            isPassword = true
                        )
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

                if (loading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(size = 40.dp).align(Alignment.Center),
                            indicatorColor = LightSkyBlue,
                            strokeWidth = 4.dp
                        )
                    }
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