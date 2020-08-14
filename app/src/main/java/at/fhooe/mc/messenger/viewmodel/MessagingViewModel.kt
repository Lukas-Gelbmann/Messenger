package at.fhooe.mc.messenger.viewmodel

import android.app.Application
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import at.fhooe.mc.messenger.model.Message
import at.fhooe.mc.messenger.model.MessageRepository
import com.bumptech.glide.Glide

class MessagingViewModel(application: Application, private var conversationId: String, private var userId: String) : ViewModel() {

    private var messageRepository =
        MessageRepository(application)
    val messages: LiveData<List<Message>> = messageRepository.getMessages(conversationId)

    fun sendMessage(content: String) {
        if (content == "")
            return // don't send empty messages

        messageRepository.sendMessage(content, conversationId, userId)
    }

    fun messageSentByCurrentUser(senderId: String): Boolean = senderId == userId

    fun getSenderName(senderId: String): String {
        val participant = messageRepository.getParticipant(senderId)
        return participant.firstName + " " + participant.lastName
    }

    fun getSenderImageUrl(senderId: String): String = messageRepository.getParticipant(senderId).avatar

    companion object{
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, url: String?) {
            if (!url.isNullOrEmpty())
                Glide.with(view.context).load(url).into(view)
        }
    }

}
