package at.fhooe.mc.messenger.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MessagingViewModelFactory(application: Application, conversationID: String, userID: String) :
    ViewModelProvider.Factory {

    private var application: Application = application
    private var conversationID: String = conversationID
    private var userID: String = userID


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MessagingViewModel(
        application,
        conversationID,
        userID
    ) as T
}