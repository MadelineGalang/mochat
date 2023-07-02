package com.dm.mochat.watch.presentation.views.contact

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dm.mochat.watch.presentation.navigation.AppRouter
import com.dm.mochat.watch.presentation.navigation.Screen

class ContactsViewModel : ViewModel() {
    private val TAG = ContactsViewModel::class.simpleName
    lateinit var navigationScreen: Screen
    var indivdualGroupIndex:Int = 0
    lateinit var icon: ImageVector
    lateinit var iconDescription: String
    lateinit var header: String
    lateinit var data: String
    lateinit var individualHeader: String
    lateinit var groupHeader: String
    private val _name = MutableLiveData("")
    val name:LiveData<String> = _name
    private val _email = MutableLiveData("")
    val email:LiveData<String> = _email
    private val _groupName = MutableLiveData("")
    val groupName:LiveData<String> = _groupName

    fun changeScreen(){
        AppRouter.navigateTo(navigationScreen)
    }

    fun navigateContacts(individualGroup:Boolean) {
        if (individualGroup) {
            icon = Icons.Filled.PersonAdd
            iconDescription= "Add Person"
            header = "Contacts"
            data = "Person"
            navigationScreen = Screen.AddEditIndividualScreen
        }
        else
        {
            icon = Icons.Filled.GroupAdd
            iconDescription= "Add Group"
            header = "Groups"
            data = "Group"
            navigationScreen =  Screen.AddEditGroupScreen
        }
        AppRouter.navigateTo(Screen.IndividualGroupContactScreen)
    }

    fun AddIndividualGroup(){
        individualHeader = "New Contact"
        groupHeader= "New Group"
        changeScreen()
    }

    fun EditIndividualGroup(index:Int){
        indivdualGroupIndex = index
        individualHeader = "Edit Contact"
        groupHeader= "Edit Group"
        changeScreen()
    }

    fun updateName(newName:String) {
        _name.value = newName
    }
    fun updateEmail(newEmail:String) {
        _email.value = newEmail
    }
    fun updateGroupName(newGroupName:String) {
        _groupName.value = newGroupName
    }
    fun addNewContact(){
        Log.d(TAG, "=======ADD CONTACT ========")
        Log.d(TAG, "NAME : ${_name.value }")
        Log.d(TAG, "email : ${_email.value }")
        Log.d(TAG, "=========================")
        AppRouter.navigateTo(Screen.IndividualGroupContactScreen)
    }
    fun deleteContact(){
        Log.d(TAG, "=======DELETE CONTACT ========")
        Log.d(TAG, "index : ${indivdualGroupIndex}")
        Log.d(TAG, "NAME : ${_name.value }")
        Log.d(TAG, "email : ${_email.value }")
        Log.d(TAG, "=========================")
        AppRouter.navigateTo(Screen.IndividualGroupContactScreen)
    }
    fun editContact(){
        Log.d(TAG, "=======EDIT CONTACT ========")
        Log.d(TAG, "index : ${indivdualGroupIndex}")
        Log.d(TAG, "NAME : ${_name.value }")
        Log.d(TAG, "email : ${_email.value }")
        Log.d(TAG, "=========================")
        AppRouter.navigateTo(Screen.IndividualGroupContactScreen)
    }
    fun addNewGroup(){
        Log.d(TAG, "=======New Group ========")
        Log.d(TAG, "group : ${_groupName.value}")
        Log.d(TAG, "=========================")
        AppRouter.navigateTo(Screen.IndividualGroupContactScreen)
    }
    fun deleteGroup(){
        Log.d(TAG, "=======delete Group ========")
        Log.d(TAG, "index : ${indivdualGroupIndex}")
        Log.d(TAG, "=========================")
        AppRouter.navigateTo(Screen.IndividualGroupContactScreen)
    }
    fun editGroup(){
        Log.d(TAG, "=======edit Group ========")
        Log.d(TAG, "index : ${indivdualGroupIndex}")
        Log.d(TAG, "=========================")
        AppRouter.navigateTo(Screen.IndividualGroupContactScreen)
    }
}


