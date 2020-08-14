package at.fhooe.mc.messenger.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.mc.messenger.R
import at.fhooe.mc.messenger.model.Conversation
import at.fhooe.mc.messenger.model.Message
import at.fhooe.mc.messenger.model.MessagingViewModel
import at.fhooe.mc.messenger.model.MessagingViewModelFactory

class MessagingActivity : AppCompatActivity() {

    private lateinit var mModel: MessagingViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MessageAdapter
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var messageText: EditText

    private lateinit var userId: String
    private lateinit var conversation: Conversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)

        viewManager = LinearLayoutManager(applicationContext)

        viewManager.stackFromEnd = true // "scroll to bottom"

        viewAdapter = MessageAdapter()

        recyclerView = findViewById(R.id.message_list)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter

        messageText = findViewById(R.id.edit_text_message)
        findViewById<Button>(R.id.button_send_message).setOnClickListener {
            val success = mModel.sendMessage(messageText.text.toString())
            if (success) messageText.setText("")
        }

        userId = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("userId", "0")!!
        conversation = intent.getParcelableExtra<Conversation>("CONVERSATION_ID")!!
        title = conversation.topic

        val model: MessagingViewModel by viewModels {
            MessagingViewModelFactory(
                application,
                conversation.id,
                userId
            )
        }

        model.messages.observe(this, Observer<List<Message>> { messages ->
            viewAdapter.setMessages(messages)
            viewAdapter.viewModel = mModel
        })
        mModel = model
    }

}
