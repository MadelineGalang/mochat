package com.dm.mochat.watch.presentation.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen {
    object StartScreen : Screen()
    object RegisterScreen : Screen()
    object LoginScreen : Screen()
    object HomeScreen : Screen()
    object ContactsScreen: Screen()
    object IndividualGroupContactScreen: Screen()
    object AddEditIndividualScreen: Screen()
    object AddEditGroupScreen: Screen()
    object ManageGroupMembersScreen: Screen()
    object RecipientScreen: Screen()
    object ChatScreen: Screen()
    object MessageGestureScreen: Screen()
    object ConfirmMessageScreen: Screen()

    object Data{
        var map = mapOf<String, String>()
    }

}

object AppRouter {
    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.StartScreen)

    fun navigateTo(destination:Screen, data: Map<String, String> = mapOf()){
        currentScreen.value = destination
        Screen.Data.map = data
    }
}