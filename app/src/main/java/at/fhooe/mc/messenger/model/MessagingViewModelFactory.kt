package at.fhooe.mc.messenger.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MessagingViewModelFactory(application: Application, conversationID: String, userID: String) :
    ViewModelProvider.Factory {

    private var mApplication: Application = application
    private var MconversationID: String = conversationID
    private var MuiserId: String = userID


    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MessagingViewModel(mApplication, MconversationID, MuiserId) as T
}