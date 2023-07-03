package com.dm.mochat.watch.presentation.views.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.dm.mochat.watch.presentation.components.*
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.navigation.SystemBackButtonHandler
import com.dm.mochat.watch.presentation.theme.BlackPearl
import com.dm.mochat.watch.presentation.theme.LightCyan
import com.dm.mochat.watch.presentation.theme.LightSkyBlue


@Composable
fun ContactsScreen(contactViewModel: ContactsViewModel = viewModel()) {
    Scaffold(
        timeText = { TimeText() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(20.dp),
        ) {
            IconButtonComponent(
                iconVector = Icons.Filled.Person,
                description = "Individual",
                onButtonClick = { contactViewModel.navigateContacts(true) },
            )
            Spacer(modifier = Modifier.width(10.dp))
            IconButtonComponent(
                iconVector = Icons.Filled.Groups,
                description = "Group",
                onButtonClick = { contactViewModel.navigateContacts(false) },
                primaryColor = false
            )
        }
    }
    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.HomeScreen)
    }
}

@Composable
fun IndividualGroupContactScreen(contactViewModel: ContactsViewModel = viewModel()) {
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
            contentPadding = PaddingValues(
                top = 10.dp,
                bottom = 40.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = scalingLazyListState
        ) {
            item {
                IconButtonComponent(
                    iconVector = contactViewModel.icon,
                    description = contactViewModel.iconDescription,
                    onButtonClick = { contactViewModel.AddIndividualGroup() }
                )
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item { NormalTextComponent(text = contactViewModel.header, color = LightSkyBlue) }
            items(10) { index ->
                Chip(
                    colors = ChipDefaults.chipColors(
                        backgroundColor = BlackPearl,
                        contentColor = LightCyan
                    ),
                    modifier = Modifier
                        .width(140.dp)
                        .padding(top = 10.dp),
                    label = {
                        NormalTextComponent(
                            text = contactViewModel.data + " ${index + 1}",
                            color = LightCyan,
                            alignment = TextAlign.Left
                        )
                    },
                    onClick = { contactViewModel.EditIndividualGroup(index) }
                )
            }
        }
    }
    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.ContactsScreen)
    }
}

@Composable
fun AddEditIndividualScreen( contactViewModel: ContactsViewModel = viewModel()) {
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
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = scalingLazyListState
        ) {
            item { LargeTextComponent(text = contactViewModel.individualHeader, color = LightCyan) }

            item {
                TextFieldComponent(placeholder = "Name", onTextChange = {
                    contactViewModel.updateName(it)
                }, icon = Icons.Filled.Person)
            }

            item {
                TextFieldComponent(placeholder = "Email", onTextChange = {
                    contactViewModel.updateEmail(it)
                }, icon = Icons.Filled.Email)
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                )
                {
                    if (contactViewModel.individualHeader == "New Contact") {
                        IconButtonComponent(
                            iconVector = Icons.Filled.Close,
                            description = "Cancel",
                            onButtonClick = { AppRouter.navigateTo(Screen.IndividualGroupContactScreen) },
                            primaryColor = false
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        IconButtonComponent(
                            iconVector = Icons.Filled.Check,
                            description = "Save",
                            onButtonClick = { contactViewModel.addNewContact() },
                        )
                    } else {
                        IconButtonComponent(
                            iconVector = Icons.Filled.Delete,
                            description = "Delete",
                            onButtonClick = { contactViewModel.deleteContact() },
                            primaryColor = false
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        IconButtonComponent(
                            iconVector = Icons.Filled.Check,
                            description = "Save",
                            onButtonClick = { contactViewModel.editContact() },
                        )
                    }

                }
            }

        }
    }
    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.IndividualGroupContactScreen)
    }
}

@Composable
fun AddEditGroupScreen( contactViewModel: ContactsViewModel = viewModel()) {
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
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = scalingLazyListState
        ) {
            item { LargeTextComponent(text = contactViewModel.groupHeader, color = LightCyan) }
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item {
                TextFieldComponent(
                    placeholder = "Group",
                    onTextChange = { contactViewModel.updateGroupName(it) },
                    icon = Icons.Filled.Group
                )
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item { LargeTextComponent(text = "Members", color = LightCyan) }
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item {
                ButtonComponent(
                    text = "MANAGE MEMBERS",
                    onButtonClick = { AppRouter.navigateTo(Screen.ManageGroupMembersScreen) },
                    textColor = BlackPearl,
                    buttonColor = LightSkyBlue
                )
            }
            items(2) { index ->
                Chip(
                    colors = ChipDefaults.chipColors(
                        backgroundColor = BlackPearl,
                        contentColor = LightCyan
                    ),
                    modifier = Modifier
                        .width(140.dp)
                        .padding(top = 10.dp),
                    label = {
                        NormalTextComponent(
                            text = "Member ${index + 1}",
                            color = LightCyan,
                            alignment = TextAlign.Left
                        )
                    },
                    onClick = {}
                )
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                )
                {
                    if (contactViewModel.groupHeader == "New Group") {
                        IconButtonComponent(
                            iconVector = Icons.Filled.Close,
                            description = "Cancel",
                            onButtonClick = { AppRouter.navigateTo(Screen.IndividualGroupContactScreen) },
                            primaryColor = false
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        IconButtonComponent(
                            iconVector = Icons.Filled.Check,
                            description = "Save",
                            onButtonClick = { contactViewModel.addNewGroup() },
                        )
                    } else {
                        IconButtonComponent(
                            iconVector = Icons.Filled.Delete,
                            description = "Delete",
                            onButtonClick = { contactViewModel.deleteGroup() },
                            primaryColor = false
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        IconButtonComponent(
                            iconVector = Icons.Filled.Check,
                            description = "Save",
                            onButtonClick = { contactViewModel.editGroup() },
                        )
                    }
                }
            }
        }
    }
    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.IndividualGroupContactScreen)
    }
}

@Composable
fun ManageGroupMembersScreen(){
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
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = scalingLazyListState
        ) {
            item { LargeTextComponent(text = "Manage Members", color = LightCyan) }
            //todo: add checklist w members names
        }
    }
    SystemBackButtonHandler {
        AppRouter.navigateTo(Screen.AddEditGroupScreen)
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun ContactScreenPreview() {
    ContactsScreen()
}