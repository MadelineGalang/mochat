package com.dm.mochat.watch.presentation.views.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterViewModel : ViewModel() {
    private val TAG = RegisterViewModel::class.simpleName

    private val auth:FirebaseAuth = Firebase.auth

    private val _name = MutableLiveData("")
    val name:LiveData<String> = _name

    private val _email = MutableLiveData("")
    val email:LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password:LiveData<String> = _password

    private val _loading = MutableLiveData(false)
    val loading:LiveData<Boolean> = _loading


    fun updateName(newName:String) {
        _name.value = newName
    }

    fun updateEmail(newEmail:String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword:String) {
        _password.value = newPassword
    }

    fun register() {
        createUserInFirebase(
            name = _name.value ?: throw IllegalArgumentException("name expected"),
            email = _email.value ?: throw IllegalArgumentException("email expected"),
            password = _password.value ?: throw IllegalArgumentException("password expected")
        )
    }

    private fun createUserInFirebase(name:String, email:String, password:String) {
        if(_loading.value == false) {
            _loading.value = true

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    Log.d(TAG, "create user completed")
                    Log.d(TAG, "isSuccessful = ${it.isSuccessful}")

                    if(it.isSuccessful) {
                        addUserName(name)
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "create user failed")
                    Log.d(TAG, "Exception = ${it.message}")
                    Log.d(TAG, "Exception = ${it.localizedMessage}")

                    _loading.value = false
                }
        }
    }

    private fun addUserName(name:String) {
        val user = auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "user name added")

                    addUserToDatabase()
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "add user name failed")
                Log.d(TAG, "Exception = ${it.message}")
                Log.d(TAG, "Exception = ${it.localizedMessage}")
            }
    }

    private fun addUserToDatabase() {
        val user = auth.currentUser

        if (user != null) {
            Firebase.firestore.collection("users").document().set(
                hashMapOf(
                    "uid" to user.uid,
                    "email" to user.email,
                    "name" to user.displayName
                )
            ).addOnSuccessListener {
                AppRouter.navigateTo(Screen.HomeScreen)

                _name.value = ""
                _email.value = ""
                _password.value = ""
                _loading.value = false
            }
        }
    }
}