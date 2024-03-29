package com.dm.mochat.watch.presentation.views.chat

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dm.mochat.watch.core.Gesture
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _recipients = MutableLiveData(emptyList<Map<String, Any>>().toMutableList())
    val recipients: LiveData<MutableList<Map<String, Any>>> = _recipients

    private val _recipientEmail = MutableLiveData("")

    private val _recipientName = MutableLiveData("")
    val recipient: LiveData<String> = _recipientName

    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    private val _isDetecting = MutableLiveData(false)
    val isDetecting: LiveData<Boolean> = _isDetecting

    private val _timerProgress = MutableLiveData(1.0F)
    val timerProgress: LiveData<Float> = _timerProgress

    private var countDownTimer: CountDownTimer? = null

    fun getRecipients() {
        Firebase.firestore.collection("users")
            .orderBy("name")
            .get()
            .addOnSuccessListener {value ->
                val list = emptyList<Map<String, Any>>().toMutableList()

                if (value != null) {
                    for (doc in value) {
                        if (doc.data["email"] != Firebase.auth.currentUser?.email) {
                            list.add(doc.data)
                        }
                    }
                }

                updateRecipients(list)
            }
    }

    private fun updateRecipients(list: MutableList<Map<String, Any>>) {
        _recipients.value = list
    }

    fun selectRecipient(email: String, name: String) {
        _recipientEmail.postValue(email)
        _recipientName.postValue(name)
    }

    fun updateMessage(newMessage: String) {
        _message.postValue(newMessage)
    }

    fun simulateGesture() {
    }


    fun sendMessage() {
        val message: String = _message.value ?: throw IllegalArgumentException("message empty")

        if (message.isNotEmpty()) {
            val sentByMap = hashMapOf<String, Any>(
                "email" to auth.currentUser?.email as String,
                "name" to auth.currentUser?.displayName as String
            )

            Firebase.firestore.collection("messages").document().set(
                hashMapOf(
                    "message" to message,
                    "sent_by" to sentByMap,
                    "sent_to" to _recipientEmail.value,
                    "sent_on" to System.currentTimeMillis()
                )
            ).addOnSuccessListener {
                _message.value = ""
                AppRouter.navigateTo(Screen.HomeScreen)
            }
        }
    }


    fun getMessageFromGesture(gesture: Gesture):String {
        return when(gesture){
            Gesture.Left -> "Goodbye"
            Gesture.Right -> "Hello"
            Gesture.Up -> "Yes"
            Gesture.Down -> "No"
            Gesture.CircleIn -> "I don't quite understand"
            Gesture.CircleOut -> "Please continue"
            else -> throw IllegalArgumentException("unknown gesture")
        }
    }
}