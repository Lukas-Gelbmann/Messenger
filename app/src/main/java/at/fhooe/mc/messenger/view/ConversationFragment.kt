package at.fhooe.mc.messenger.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import at.fhooe.mc.messenger.R
import at.fhooe.mc.messenger.model.AppDatabase
import at.fhooe.mc.messenger.model.Conversation
import at.fhooe.mc.messenger.model.GetConversationService
import at.fhooe.mc.messenger.view.ConversationAdapter.ClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ConversationFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ConversationAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var conversations: List<Conversation>
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val retrofit = Retrofit.Builder().baseUrl(MainActivity.serverIp).addConverterFactory(GsonConverterFactory.create()).build()
    lateinit var db: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.conversation_view, container, false)
        db = AppDatabase.getDatabase(context)
        swipeRefreshLayout = view.findViewById(R.id.swipe_container) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewManager = LinearLayoutManager(context)
        viewAdapter = ConversationAdapter()
        viewAdapter.setOnItemClickListener(object : ClickListener {
            override fun onItemClick(position: Int, view: View) {
                val conversation = viewAdapter.getConversation(position)
                openConversation(conversation)
            }
        })
        recyclerView = requireView().findViewById<RecyclerView>(R.id.conversation_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        fetchAllConversations()
    }

    override fun onRefresh() {
        fetchAllConversations()
    }

    private fun fetchAllConversations() {
        var conversationCount: Int
        val conversationService: GetConversationService = retrofit.create(GetConversationService::class.java)
        val conversationsCountCall: Call<Int> = conversationService.fetchConversationCount()
        conversationsCountCall.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    conversationCount = response.body()!!
                    fetchAllConversationsPages(conversationCount/30)
                } else {
                    conversations = db.conversationDao().conversations
                    viewAdapter.setConversations(conversations)
                    swipeRefreshLayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                conversations = db.conversationDao().conversations
                viewAdapter.setConversations(conversations)
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun fetchAllConversationsPages(i: Int) {
        val conversationService: GetConversationService = retrofit.create(GetConversationService::class.java)
        for (page in 0..i) {
            val conversationsCall: Call<List<Conversation>> = conversationService.fetchAllConversations(page)
            conversationsCall.enqueue(object : Callback<List<Conversation>> {
                override fun onResponse(call: Call<List<Conversation>>, response: Response<List<Conversation>>) {
                    if (response.isSuccessful) {
                        val conversations = response.body()!!
                        for (conversation in conversations)
                            db.conversationDao()?.insert(conversation)
                    }
                    if (page == i)
                        conversations = db.conversationDao().conversations
                        viewAdapter.setConversations(conversations)
                        swipeRefreshLayout.isRefreshing = false
                }

                override fun onFailure(call: Call<List<Conversation>>, t: Throwable) {
                    if (page == i) {
                        conversations = db.conversationDao().conversations
                        viewAdapter.setConversations(conversations)
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            })
        }
    }

    private fun openConversation(conversation: Conversation) {
        val intent = Intent(context, MessagingActivity::class.java)
        intent.putExtra("CONVERSATION_ID", conversation)
        startActivity(intent)
    }

}
