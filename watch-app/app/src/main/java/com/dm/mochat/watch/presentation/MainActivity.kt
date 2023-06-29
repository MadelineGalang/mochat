/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.dm.mochat.watch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.dm.mochat.watch.presentation.views.home.HomeScreen
import com.dm.mochat.watch.presentation.views.login.LoginScreen
import com.dm.mochat.watch.presentation.views.register.RegisterScreen
import com.dm.mochat.watch.presentation.views.start.StartScreen
import com.dm.mochat.watch.presentation.theme.MoChatWatchTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    MoChatWatchTheme {
        Crossfade(targetState = AppRouter.currentScreen) {currentState ->
            when(currentState.value) {
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
            }
        }
    }
}
