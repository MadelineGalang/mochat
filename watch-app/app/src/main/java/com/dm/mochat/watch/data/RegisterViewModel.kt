package com.dm.mochat.watch.data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {
    private val TAG = RegisterViewModel::class.simpleName

    var registerUIState = mutableStateOf(RegisterUIState())

    fun onEvent(event:RegisterUIEvent) {
        when(event) {
            is RegisterUIEvent.NameChanged -> {
                registerUIState.value = registerUIState.value.copy(
                    name = event.name
                )
            }
            is RegisterUIEvent.EmailChanged -> {
                registerUIState.value = registerUIState.value.copy(
                    email = event.email
                )
            }
            is RegisterUIEvent.PasswordChanged -> {
                registerUIState.value = registerUIState.value.copy(
                    password = event.password
                )
            }
            is RegisterUIEvent.RegisterButtonClicked -> {
                register()
            }
        }
    }

    private fun register() {
        createUserInFirebase(
            email = registerUIState.value.email,
            password = registerUIState.value.password
        )
    }

    private fun createUserInFirebase(email:String, password:String) {

        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Log.d(TAG, "create user completed")
                Log.d(TAG, "isSuccessful = ${it.isSuccessful}")


                if(it.isSuccessful) {
                    AppRouter.navigateTo(Screen.HomeScreen)
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "create user failed")
                Log.d(TAG, "Exception = ${it.message}")
                Log.d(TAG, "Exception = ${it.localizedMessage}")
            }
    }
}