package at.fhooe.mc.messenger.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import at.fhooe.mc.messenger.LoginActivity
import at.fhooe.mc.messenger.R
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.conversation_view, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_container) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //create listview
        viewManager = LinearLayoutManager(context)
        viewAdapter = ConversationAdapter()
        viewAdapter.setOnItemClickListener(object : ClickListener {
            override fun onItemClick(position: Int, view: View) {
                Log.d("xdd", "onItemClick position: " + position)
                openConversation(position)
            }
        })

        recyclerView = requireView().findViewById<RecyclerView>(R.id.conversation_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        fetchConversations()
    }


    override fun onRefresh() {
        fetchConversations();
    }

    private fun fetchConversations() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.0.29:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val conversationService: GetConversationService =
            retrofit.create(GetConversationService::class.java)
        val conversationsCall: Call<List<Conversation>> =
            conversationService.fetchAllConversations()
        conversationsCall.enqueue(object : Callback<List<Conversation>> {
            override fun onResponse(
                call: Call<List<Conversation>>,
                response: Response<List<Conversation>>
            ) {
                if (!response.isSuccessful)
                    return
                conversations = response.body()!!
                viewAdapter.setConversations(conversations)
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Conversation>>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(context, "fetching data failed", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun openConversation(position: Int) {
        startActivity(Intent(context, MessagingActivity::class.java))
    }

}
