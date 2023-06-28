package com.dm.mochat.watch.data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {
    private val TAG = HomeViewModel::class.simpleName

    var homeUIState = mutableStateOf(HomeUIState())

    fun getCurrentUser() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            for (profile in it.providerData) {
                homeUIState.value = homeUIState.value.copy(
                    currentUserEmail = if(profile.email == null) "" else profile.email!!,
                    currentUserName = if(profile.displayName == null) "" else profile.displayName!!
                )
            }
        }

    }

    fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signOut()

        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                Log.d(TAG, "logout success")
                AppRouter.navigateTo(Screen.StartScreen)
            } else {
                Log.d(TAG, "logout failed")
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)
    }
}