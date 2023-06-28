package com.dm.mochat.watch.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import com.dm.mochat.watch.data.RegisterUIEvent
import com.dm.mochat.watch.data.RegisterViewModel
import com.dm.mochat.watch.presentation.components.LargeTextComponent
import com.dm.mochat.watch.presentation.components.IconButtonComponent
import com.dm.mochat.watch.presentation.components.TextFieldComponent
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.navigation.SystemBackButtonHandler
import com.dm.mochat.watch.presentation.theme.LightCyan

@Composable
fun RegisterScreen(registerViewModel:RegisterViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LargeTextComponent(text = "Register", color = LightCyan)
        Spacer(modifier = Modifier.height(10.dp))

        TextFieldComponent(placeholder = "Name", onTextChange = {
            registerViewModel.onEvent(RegisterUIEvent.NameChanged(it))
        })
        Spacer(modifier = Modifier.height(10.dp))

        TextFieldComponent(placeholder = "Email", onTextChange = {
            registerViewModel.onEvent(RegisterUIEvent.EmailChanged(it))
        })
        Spacer(modifier = Modifier.height(10.dp))

        TextFieldComponent(placeholder = "Password", onTextChange = {
            registerViewModel.onEvent(RegisterUIEvent.PasswordChanged(it))
        }, isPassword = true)
        Spacer(modifier = Modifier.height(10.dp))

        IconButtonComponent(
            iconVector = Icons.Filled.Check,
            description = "Register",
            onButtonClick = {
                registerViewModel.onEvent(RegisterUIEvent.RegisterButtonClicked)
            },
        )
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