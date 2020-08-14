package at.fhooe.mc.messenger.view

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.room.Room
import at.fhooe.mc.messenger.other.NewConversationDialogFragment
import at.fhooe.mc.messenger.other.NotificationJobService
import at.fhooe.mc.messenger.R
import at.fhooe.mc.messenger.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(),
    NewConversationDialogFragment.NewConversationDialogListener {

    companion object {
        const val JOB_NOTIFICATION_ID: Int = 1
        private lateinit var mScheduler: JobScheduler
        const val TAG = "Messenger"
        const val PARTICIPANT_ID = "1"
        const val DATABASE_NAME = "Messenger"
        //const val serverIp = "http://192.168.1.191:8080/"
        const val serverIp = "http://10.0.0.128:8080/"
    }

    private val retrofit = Retrofit.Builder().baseUrl(serverIp).addConverterFactory(GsonConverterFactory.create()).build()
    private var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        this.savedInstanceState = savedInstanceState
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)
        val isFirstRun =
            this.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true)
        if (isFirstRun) {
            startActivityForResult(Intent(this, LoginActivity::class.java), 0)
        } else {
            initComponents()
        }
        // load all participants into db
        fetchAllParticipants()
    }

    private fun fetchAllParticipants() {
        var participantCount: Int
        val participantService: GetParticipantService = retrofit.create(GetParticipantService::class.java)
        val participantsCountCall: Call<Int> = participantService.fetchParticipantCount()
        participantsCountCall.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    participantCount = response.body()!!
                    fetchAllParticipantsPages(participantCount/30)
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
            }
        })
    }

    private fun fetchAllParticipantsPages(i: Int) {
        val db =
            application.let {
                Room.databaseBuilder(it, AppDatabase::class.java,
                    DATABASE_NAME
                )
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
                            db.participantDao().insert(participant)
                    }
                }

                override fun onFailure(call: Call<List<Participant>>, t: Throwable) {
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0)
            initComponents()
    }

    private fun initComponents() {
        mScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val serviceName = ComponentName(packageName, NotificationJobService::class.java.name)
        val builder = JobInfo.Builder(JOB_NOTIFICATION_ID, serviceName).setMinimumLatency(10000)
        val myJobInfo = builder.build()
        mScheduler.schedule(myJobInfo)



        if (savedInstanceState == null) {
            val fragment = ConversationFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                .commit()
        }

        val bottomNavigationView = findViewById<View>(R.id.bottom_nav_view) as BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_conversations -> {
                    val fragment = ConversationFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()
                }
                R.id.action_users -> {
                    val fragment = ParticipantFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()
                }
            }
            true
        }

    }

    fun createNewConversation(view: View) {
        val dialog = NewConversationDialogFragment()
        dialog.show(supportFragmentManager, "NewConversationDialogFragment")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, topic: String) {
        val service =  retrofit.create(PostConversationService::class.java)
        val conversation = Conversation(topic)
        val call: Call<Conversation> = service.sendConversation(conversation)
        call.enqueue(object : Callback<Conversation?> {
            override fun onResponse(
                call: Call<Conversation?>?,
                response: Response<Conversation?>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, getString(R.string.new_topic_created), Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<Conversation?>, t: Throwable) {
                Toast.makeText(applicationContext, getString(R.string.creating_topic_failed), Toast.LENGTH_LONG).show()
            }
        })

    }
}