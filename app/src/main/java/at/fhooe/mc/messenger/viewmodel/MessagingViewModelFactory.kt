package at.fhooe.mc.messenger.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MessagingViewModelFactory(application: Application, conversationID: String, userID: String) :
    ViewModelProvider.Factory {

    private var mApplication: Application = application
    private var mConversationID: String = conversationID
    private var mUserID: String = userID


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MessagingViewModel(
        mApplication,
        mConversationID,
        mUserID
    ) as T
}