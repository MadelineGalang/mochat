package com.dm.mochat.watch.presentation.views.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import com.dm.mochat.watch.presentation.components.LargeTextComponent
import com.dm.mochat.watch.presentation.components.IconButtonComponent
import com.dm.mochat.watch.presentation.components.TextFieldComponent
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.navigation.SystemBackButtonHandler
import com.dm.mochat.watch.presentation.theme.LightCyan

@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel = viewModel()) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LargeTextComponent(text = "Register", color = LightCyan)

            TextFieldComponent(placeholder = "Name", onTextChange = {
                registerViewModel.updateName(it)
            })

            TextFieldComponent(placeholder = "Email", onTextChange = {
                registerViewModel.updateEmail(it)
            })

            TextFieldComponent(placeholder = "Password", onTextChange = {
                registerViewModel.updatePassword(it)
            }, isPassword = true)

            IconButtonComponent(
                iconVector = Icons.Filled.Check,
                description = "Register",
                onButtonClick = {
                    registerViewModel.register()
                },
            )
        }

        if(registerViewModel.loading.value == true) {
            CircularProgressIndicator()
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