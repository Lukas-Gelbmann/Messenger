package at.fhooe.mc.messenger.model

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import at.fhooe.mc.messenger.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageRepository(private val application: Application) {


    private val messages: MutableLiveData<List<Message>> = MutableLiveData<List<Message>>()

    private val retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.serverIp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getMessages(conversationId: String): LiveData<List<Message>> {
        fetchMessages(conversationId)
        return messages
    }

    private fun fetchMessages(conversationId: String) {
        val db =
            application.let {
                Room.databaseBuilder(it, AppDatabase::class.java, "Messenger")
                    .allowMainThreadQueries().build()
            }


        val messageService = retrofit.create(GetMessageService::class.java)

        val messagesCall: Call<List<Message>> =
            messageService.getMessagesForConversation(conversationId)
        messagesCall.enqueue(object : Callback<List<Message>> {
            override fun onResponse(
                call: Call<List<Message>>,
                response: Response<List<Message>>
            ) {
                if (response.isSuccessful) {
                    messages.value = response.body()!!
                    for (mess in messages.value!!)
                        db.messageDao().insert(mess)
                } else {
                    messages.value = db.messageDao().getMessages(conversationId)
                }

            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Log.e(MainActivity.TAG, "fetching data failed")
                messages.value = db.messageDao().getMessages(conversationId)
            }
        })
    }


    fun sendMessage(content: String, conversationId: String, userId: String) {
        val service: PostMessageService = retrofit.create(PostMessageService::class.java)

        val message = Message(content, conversationId, userId, MainActivity.PARTICIPANT_ID)

        val call: Call<Message> = service!!.createMessage(message)
        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message?>?,
                response: Response<Message?>
            ) {
                if (response.isSuccessful) {
                    // refresh messages
                    messages.postValue(getMessages(conversationId).value)
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {

                Log.e(MainActivity.TAG, "sending data failed")
            }
        })
    }

    // get participant from db
    fun getParticipant(id: String): Participant {
        val db =
            application.let {
                Room.databaseBuilder(it, AppDatabase::class.java, "Messenger")
                    .allowMainThreadQueries().build()
            }

        return db.participantDao().getParticipant(id)
    }

}