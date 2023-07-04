package com.dm.mochat.watch.presentation.views.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    private val _recipient = MutableLiveData("")
    val recipient: LiveData<String> = _recipient

    fun selectRecipient(name:String) {
        _recipient.value = name
    }
}