package at.fhooe.mc.messenger.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class MessagingViewModel(
    application: Application,
    private var conversationId: String,
    private var userId: String
) : ViewModel() {

    private var messageRepository = MessageRepository(application)

    val messages: LiveData<List<Message>> = messageRepository.getMessages(conversationId)

    fun sendMessage(content: String) {
        if (content == "")
            return // don't send empty messages

        messageRepository.sendMessage(content, conversationId, userId)
    }
}
