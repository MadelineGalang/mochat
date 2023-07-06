package com.dm.mochat.watch.presentation.views.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class HomeViewModel : ViewModel() {

    private val TAG = HomeViewModel::class.simpleName

    private val auth:FirebaseAuth = Firebase.auth

    private val _currentUserEmail = MutableLiveData("")
    val currentUserEmail:LiveData<String> = _currentUserEmail

    private val _currentUserName = MutableLiveData("")
    val currentUserName:LiveData<String> = _currentUserName

    private val _messages = MutableLiveData(emptyList<Map<String, Any>>().toMutableList())
    val messages: LiveData<MutableList<Map<String, Any>>> = _messages

    fun getCurrentUser() {
        val currentUser = auth.currentUser
        currentUser?.let{
            for (profile in it.providerData) {
                _currentUserEmail.value = profile.email
                _currentUserName.value = profile.displayName
            }
        }
    }

    fun getMessages() {
        val dateFormat = SimpleDateFormat("MM/dd/yy")

        Firebase.firestore.collection("messages")
            .whereEqualTo("sent_to", _currentUserEmail.value)
            .orderBy("sent_on")
            .addSnapshotListener { value, _ ->
                val list = emptyList<Map<String, Any>>().toMutableList()

                if (value != null) {
                    for (doc in value) {
                        val data = doc.data

                        val stringDate = dateFormat.format(Date(data["sent_on"] as Long))
                        data["sent_on"] = stringDate

                        getUserNameByEmail(data["sent_by"] as String) { name ->
                            data["sent_by"] = name

                            list.add(data)

                            if (list.size == value.size()) {
                                updateMessages(list)
                            }
                        }
                    }
                } else {
                    updateMessages(list)
                }
            }
    }

    private fun getUserNameByEmail(email: String, callback: (String?) -> Unit) {
        Firebase.firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { value ->
                if (!value.isEmpty) {
                    callback(value.documents[0].getString("name"))
                } else {
                    callback("Unknown")
                }
            }
    }

    private fun updateMessages(list: MutableList<Map<String, Any>>) {
        _messages.value = list.asReversed()
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