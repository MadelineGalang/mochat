package com.dm.mochat.watch.presentation.views.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {
    private val TAG = LoginViewModel::class.simpleName

    private val auth: FirebaseAuth = Firebase.auth

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading


    fun updateEmail(newEmail:String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword:String) {
        _password.value = newPassword
    }

    fun login() {
        loginUserWithFirebase(
            email = _email.value ?: throw IllegalArgumentException("email expected"),
            password = _password.value ?: throw IllegalArgumentException("password expected")
        )
    }

    private fun loginUserWithFirebase(email:String, password:String) {
        if(_loading.value == false) {
            _loading.value = true

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    Log.d(TAG, "login completed")
                    Log.d(TAG, "isSuccessful = ${it.isSuccessful}")

                    if(it.isSuccessful) {
                        AppRouter.navigateTo(Screen.HomeScreen)
                    }

                    _loading.value = false
                }
                .addOnFailureListener {
                    Log.d(TAG, "login failed")
                    Log.d(TAG, "Exception = ${it.message}")
                    Log.d(TAG, "Exception = ${it.localizedMessage}")

                    _loading.value = false
                }
        }
    }
}