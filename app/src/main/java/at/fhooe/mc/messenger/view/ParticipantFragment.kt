package at.fhooe.mc.messenger.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import at.fhooe.mc.messenger.R
import at.fhooe.mc.messenger.model.AppDatabase
import at.fhooe.mc.messenger.model.GetParticipantService
import at.fhooe.mc.messenger.model.Participant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ParticipantFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ParticipantAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var participants: List<Participant>
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val retrofit = Retrofit.Builder().baseUrl(MainActivity.serverIp).addConverterFactory(GsonConverterFactory.create()).build()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.participant_view, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_container) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewManager = LinearLayoutManager(context)
        viewAdapter = ParticipantAdapter(context)
        recyclerView = requireView().findViewById<RecyclerView>(R.id.participant_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        fetchAllParticipants()
    }

    override fun onRefresh() {
        fetchAllParticipants()
    }

    private fun fetchAllParticipants() {
        val db = context?.let {
            Room.databaseBuilder(it, AppDatabase::class.java, MainActivity.DATABASE_NAME)
                .allowMainThreadQueries().build()
        }
        var participantCount: Int
        val participantService: GetParticipantService = retrofit.create(GetParticipantService::class.java)
        val participantsCountCall: Call<Int> = participantService.fetchParticipantCount()
        participantsCountCall.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    participantCount = response.body()!!
                    fetchAllParticipantsPages(participantCount/30)
                }else {
                    participants = db!!.participantDao().participants
                    viewAdapter.setParticipants(participants)
                    swipeRefreshLayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                participants = db!!.participantDao().participants
                viewAdapter.setParticipants(participants)
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun fetchAllParticipantsPages(i: Int) {
        val db = context?.let {
                Room.databaseBuilder(it, AppDatabase::class.java, MainActivity.DATABASE_NAME)
                    .allowMainThreadQueries().build()
            }
        val participantService: GetParticipantService = retrofit.create(GetParticipantService::class.java)
        for (page in 0..i) {
            val participantsCall: Call<List<Participant>> = participantService.fetchAllParticipants(page)
            participantsCall.enqueue(object : Callback<List<Participant>> {
                override fun onResponse(call: Call<List<Participant>>, response: Response<List<Participant>>) {
                    if (response.isSuccessful) {
                        val participants = response.body()!!
                        for (participant in participants)
                            db?.participantDao()?.insert(participant)
                    }
                    if (page == i) {
                        participants = db!!.participantDao().participants
                        viewAdapter.setParticipants(participants)
                        swipeRefreshLayout.isRefreshing = false
                    }
                }

                override fun onFailure(call: Call<List<Participant>>, t: Throwable) {
                    if (page == i) {
                        participants = db!!.participantDao().participants
                        viewAdapter.setParticipants(participants)
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            })
        }
    }
}
