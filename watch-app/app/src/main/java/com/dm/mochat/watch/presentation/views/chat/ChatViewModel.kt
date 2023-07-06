package com.dm.mochat.watch.presentation.views.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatViewModel : ViewModel() {
    private val _recipients = MutableLiveData(emptyList<Map<String, Any>>().toMutableList())
    val recipients: LiveData<MutableList<Map<String, Any>>> = _recipients

    private val _recipientEmail = MutableLiveData("")

    private val _recipientName = MutableLiveData("")
    val recipient: LiveData<String> = _recipientName

    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

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
        _recipientEmail.value = email
        _recipientName.value = name
    }

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun sendMessage() {
        val message: String = _message.value ?: throw IllegalArgumentException("message empty")
        if (message.isNotEmpty()) {
            Firebase.firestore.collection("messages").document().set(
                hashMapOf(
                    "message" to message,
                    "sent_by" to Firebase.auth.currentUser?.email,
                    "sent_to" to _recipientEmail.value,
                    "sent_on" to System.currentTimeMillis()
                )
            ).addOnSuccessListener {
                _message.value = ""
                AppRouter.navigateTo(Screen.HomeScreen)
            }
        }
    }
}