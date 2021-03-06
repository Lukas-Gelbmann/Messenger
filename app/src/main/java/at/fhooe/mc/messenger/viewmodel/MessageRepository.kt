package at.fhooe.mc.messenger.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.fhooe.mc.messenger.view.MainActivity
import at.fhooe.mc.messenger.R
import at.fhooe.mc.messenger.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageRepository(private val application: Application) {

    private val messages: MutableLiveData<List<Message>> = MutableLiveData()
    private val retrofit = Retrofit.Builder().baseUrl(MainActivity.serverIp).addConverterFactory(GsonConverterFactory.create()).build()
    lateinit var db: AppDatabase

    fun getMessages(conversationId: String): LiveData<List<Message>> {
        fetchAllMessages(conversationId)
        return messages
    }

    private fun fetchAllMessages(conversationId: String) {
        db = AppDatabase.getDatabase(application.applicationContext)
        var count: Int
        val service: GetParticipantService = retrofit.create(
            GetParticipantService::class.java)
        val countCall: Call<Int> = service.fetchParticipantCount()
        countCall.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    count = response.body()!!
                    fetchAllMessagesPages(count / 30, conversationId)
                } else {
                    messages.value = db.messageDao().getMessages(conversationId)

                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                messages.value = db.messageDao().getMessages(conversationId)

            }
        })
    }

    private fun fetchAllMessagesPages(i: Int, conversationId: String) {
        val service: GetMessageService = retrofit.create(
            GetMessageService::class.java)
        for (page in 0..i) {
            val call: Call<List<Message>> = service.getMessagesForConversation(conversationId, page)
            call.enqueue(object : Callback<List<Message>> {
                override fun onResponse(
                    call: Call<List<Message>>,
                    response: Response<List<Message>>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body()!!
                        for (entry in list)
                            db.messageDao().insert(entry)
                    }
                    if (page == i)
                        messages.value = db.messageDao().getMessages(conversationId)
                }

                override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                    if (page == i) {
                        Log.e(MainActivity.TAG, "fetching data failed")
                        messages.value = db.messageDao().getMessages(conversationId)
                    }
                }
            })
        }
    }

    fun sendMessage(content: String, conversationId: String, userId: String): Boolean {
        var success = false
        val db = AppDatabase.getDatabase(application)
        val service: PostMessageService = retrofit.create(
            PostMessageService::class.java)

        val message = Message(
            content,
            conversationId,
            userId,
            MainActivity.PARTICIPANT_ID
        )

        val call: Call<Message> = service.createMessage(message)
        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message?>?,
                response: Response<Message?>
            ) {
                if (response.isSuccessful) {
                    messages.postValue(getMessages(conversationId).value)
                    db.messageDao().insert(response.body()!!)
                    success = true
                } else {
                    Toast.makeText(
                        application.applicationContext,
                        application.applicationContext.getString(R.string.sending_message_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                Toast.makeText(
                    application.applicationContext,
                    application.applicationContext.getString(R.string.sending_message_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        return success
    }

    fun getParticipant(id: String): Participant {
        db = AppDatabase.getDatabase(application.applicationContext)
        return db.participantDao().getParticipant(id)
    }

}