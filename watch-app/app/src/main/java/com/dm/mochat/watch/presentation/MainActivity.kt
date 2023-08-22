/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.dm.mochat.watch.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.views.home.HomeScreen
import com.dm.mochat.watch.presentation.views.login.LoginScreen
import com.dm.mochat.watch.presentation.views.register.RegisterScreen
import com.dm.mochat.watch.presentation.views.start.StartScreen
import com.dm.mochat.watch.presentation.theme.MoChatWatchTheme
import com.dm.mochat.watch.presentation.views.chat.ChatScreen
import com.dm.mochat.watch.presentation.views.chat.ConfirmMessageScreen
import com.dm.mochat.watch.presentation.views.chat.MessageGestureScreen
import com.dm.mochat.watch.presentation.views.chat.RecipientScreen
import com.dm.mochat.watch.presentation.views.contact.ContactsScreen
import com.dm.mochat.watch.presentation.views.contact.IndividualGroupContactScreen
import com.dm.mochat.watch.presentation.views.contact.AddEditIndividualScreen
import com.dm.mochat.watch.presentation.views.contact.AddEditGroupScreen
import com.dm.mochat.watch.presentation.views.contact.ManageGroupMembersScreen
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        FirebaseApp.initializeApp(this)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    MoChatWatchTheme {
        Crossfade(targetState = AppRouter.currentScreen) { currentState ->
            when (currentState.value) {
                is Screen.StartScreen -> {
                    StartScreen()
                }

                is Screen.RegisterScreen -> {
                    RegisterScreen()
                }

                is Screen.LoginScreen -> {
                    LoginScreen()
                }

                is Screen.HomeScreen -> {
                    HomeScreen()
                }

                is Screen.ContactsScreen -> {
                    ContactsScreen()
                }

                is Screen.IndividualGroupContactScreen -> {
                    IndividualGroupContactScreen()
                }

                is Screen.AddEditIndividualScreen -> {
                    AddEditIndividualScreen()
                }

                is Screen.AddEditGroupScreen -> {
                    AddEditGroupScreen()
                }

                is Screen.ManageGroupMembersScreen -> {
                    ManageGroupMembersScreen()
                }

                is Screen.RecipientScreen -> {
                    RecipientScreen()
                }

                is Screen.ChatScreen -> {
                    ChatScreen()
                }

                is Screen.MessageGestureScreen -> {
                    MessageGestureScreen()
                }

                is Screen.ConfirmMessageScreen -> {
                    ConfirmMessageScreen()
                }
            }
        }
    }
}
