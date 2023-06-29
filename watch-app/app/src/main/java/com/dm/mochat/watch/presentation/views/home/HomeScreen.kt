package com.dm.mochat.watch.presentation.views.home

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import com.dm.mochat.watch.presentation.components.ButtonComponent
import com.dm.mochat.watch.presentation.components.LargeTextComponent
import com.dm.mochat.watch.presentation.components.NormalTextComponent
import com.dm.mochat.watch.presentation.theme.BlackPearl
import com.dm.mochat.watch.presentation.theme.LightCyan
import com.dm.mochat.watch.presentation.theme.LightSkyBlue

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    homeViewModel.getCurrentUser()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LargeTextComponent(text = "Hello, ${homeViewModel.currentUserName.value}", color = LightCyan)
        NormalTextComponent(text = homeViewModel.currentUserEmail.value!!, color = LightCyan)
        Spacer(modifier = Modifier.height(10.dp))
        ButtonComponent(
            text = "LOGOUT",
            onButtonClick = {
                homeViewModel.logout()
            },
            textColor = BlackPearl,
            buttonColor = LightSkyBlue
        )
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}