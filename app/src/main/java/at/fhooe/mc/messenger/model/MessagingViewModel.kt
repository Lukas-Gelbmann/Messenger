package at.fhooe.mc.messenger.model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.fhooe.mc.messenger.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessagingViewModel : ViewModel() {

    var userId: String = ""
    var conversationId: String = ""


    private val messages: MutableLiveData<List<Message>> by lazy {
        MutableLiveData<List<Message>>().also {
            loadMessages()
        }
    }

    fun getMessages(): LiveData<List<Message>> = messages

    fun sendMessage(content: String) {
        if (content == "")
            return // don't send empty messages

        var service: PostMessageService? = null
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(MainActivity.serverIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            service = retrofit.create(PostMessageService::class.java)
        } catch (e: Exception) {
            Log.e(MainActivity.TAG, e.toString())
        }

        val message = Message(content, conversationId, userId, "1") // TODO receiverId???

        val call: Call<Message> = service!!.createMessage(message)
        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message?>?,
                response: Response<Message?>
            ) {
                if (response.isSuccessful) {
                    loadMessages()
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                Log.e(MainActivity.TAG, "sending data failed")
            }
        })
    }

    private fun loadMessages() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity.serverIp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val messageService = retrofit.create(GetMessageService::class.java)

        val messagesCall: Call<List<Message>> =
            messageService.getMessagesForConversation(conversationId)
        messagesCall.enqueue(object : Callback<List<Message>> {
            override fun onResponse(
                call: Call<List<Message>>,
                response: Response<List<Message>>
            ) {
                if (!response.isSuccessful)
                    return

                messages.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Log.e(MainActivity.TAG, "fetching data failed")
            }
        })
    }
}