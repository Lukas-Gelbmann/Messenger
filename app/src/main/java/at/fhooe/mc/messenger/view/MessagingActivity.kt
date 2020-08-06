package at.fhooe.mc.messenger.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.mc.messenger.MainActivity
import at.fhooe.mc.messenger.R
import at.fhooe.mc.messenger.model.GetMessageService
import at.fhooe.mc.messenger.model.Message
import at.fhooe.mc.messenger.model.PostMessageService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessagingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MessageAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var messageText: EditText
    lateinit var messages: List<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)

        viewAdapter = MessageAdapter()
        viewAdapter.setOnItemClickListener(object : MessageAdapter.ClickListener {
            override fun onItemClick(position: Int, view: View) {
                Log.d("xdd", "onItemClick position: $position")
            }
        })

        viewManager = LinearLayoutManager(applicationContext)
        viewAdapter = MessageAdapter()

        recyclerView = findViewById(R.id.message_list)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter

        messageText = findViewById(R.id.edit_text_message)
        findViewById<Button>(R.id.button_send_message).setOnClickListener {
            sendMessage()
        }

        fetchMessages()

    }

    private fun fetchMessages() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity.serverIp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val messageService = retrofit.create(GetMessageService::class.java)

        val messagesCall: Call<List<Message>> =
            messageService.fetchAllMessages() // TODO only messages from conversation
        messagesCall.enqueue(object : Callback<List<Message>> {
            override fun onResponse(
                call: Call<List<Message>>,
                response: Response<List<Message>>
            ) {
                if (!response.isSuccessful)
                    return
                messages = response.body()!!
                viewAdapter.setMessages(messages)
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Toast.makeText(applicationContext, "fetching data failed", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendMessage() {
        var service: PostMessageService? = null
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(MainActivity.serverIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            service = retrofit.create(PostMessageService::class.java)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
        }

        val content = messageText.text.toString()
        val message = Message(content, "2", "4", "1")

        val call: Call<Message> = service!!.createMessage(message)
        call.enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message?>?,
                response: Response<Message?>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "message sent", Toast.LENGTH_LONG)
                        .show()
                    messageText.setText("")
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                Toast.makeText(applicationContext, "sending data failed", Toast.LENGTH_LONG).show()
            }
        })
    }

}
