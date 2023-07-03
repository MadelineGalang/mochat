package com.dm.mochat.watch.presentation.views.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {
    private val TAG = HomeViewModel::class.simpleName

    private val auth:FirebaseAuth = Firebase.auth

    private val _currentUserEmail = MutableLiveData("")
    val currentUserEmail:LiveData<String> = _currentUserEmail

    private val _currentUserName = MutableLiveData("")
    val currentUserName:LiveData<String> = _currentUserName

    fun getCurrentUser() {
        val currentUser = auth.currentUser
        currentUser?.let{
            for (profile in it.providerData) {
                _currentUserEmail.value = profile.email
                _currentUserName.value = profile.displayName
            }
        }
    }

    fun logout() {
        auth.signOut()

        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                Log.d(TAG, "logout success")
                AppRouter.navigateTo(Screen.StartScreen)
            } else {
                Log.d(TAG, "logout failed")
            }
        }

        auth.addAuthStateListener(authStateListener)
    }
}