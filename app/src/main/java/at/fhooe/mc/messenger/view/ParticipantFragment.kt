package at.fhooe.mc.messenger.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import at.fhooe.mc.messenger.R
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.participant_view, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipe_container) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //create listview
        viewManager = LinearLayoutManager(context)
        viewAdapter = ParticipantAdapter(context)

        recyclerView = requireView().findViewById<RecyclerView>(R.id.participant_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        fetchParticipants()
    }

    override fun onRefresh() {
        fetchParticipants()
    }

    private fun fetchParticipants() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.0.29:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val participantService: GetParticipantService =
            retrofit.create(GetParticipantService::class.java)
        val participantsCall: Call<List<Participant>> = participantService.fetchAllParticipants()
        participantsCall.enqueue(object : Callback<List<Participant>> {
            override fun onResponse(
                call: Call<List<Participant>>,
                response: Response<List<Participant>>
            ) {
                if (!response.isSuccessful)
                    return
                participants = response.body()!!
                viewAdapter.setParticipants(participants)
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Participant>>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(context, "fetching data failed", Toast.LENGTH_LONG).show()
            }
        })
    }
}
