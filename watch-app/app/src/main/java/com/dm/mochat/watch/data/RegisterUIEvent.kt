package com.dm.mochat.watch.data

sealed class RegisterUIEvent {
    data class NameChanged(val name:String) : RegisterUIEvent()
    data class EmailChanged(val email:String) : RegisterUIEvent()
    data class PasswordChanged(val password:String) : RegisterUIEvent()

    object RegisterButtonClicked : RegisterUIEvent()
}
