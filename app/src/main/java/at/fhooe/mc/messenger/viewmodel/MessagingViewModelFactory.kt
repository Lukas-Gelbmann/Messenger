package at.fhooe.mc.messenger.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MessagingViewModelFactory(private var application: Application, private var conversationID: String, private var userID: String) :
    ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MessagingViewModel(
        application,
        conversationID,
        userID
    ) as T
}