package at.fhooe.mc.messenger

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import at.fhooe.mc.messenger.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    private var mFirstNameEditText: EditText? = null
    private var mLastNameEditText: EditText? = null
    private var mEmailEditText: EditText? = null
    private var mCreateButton: Button? = null
    private val retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.serverIp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
     lateinit var db: AppDatabase
    var isFetchDataFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mFirstNameEditText = findViewById(R.id.text_firstname)
        mLastNameEditText = findViewById(R.id.text_lastname)
        mEmailEditText = findViewById(R.id.text_email)
        mCreateButton = findViewById(R.id.button_createuser)
        db = application.let {
                Room.databaseBuilder(it, AppDatabase::class.java, MainActivity.DATABASE_NAME)
                    .allowMainThreadQueries().build()
            }

        fetchAllMessages()
    }

    fun onClick(view: View) {
        val newParticipant = Participant(
            mFirstNameEditText?.text.toString(),
            mLastNameEditText?.text.toString(),
            mEmailEditText?.text.toString()
        )


        if (!isFetchDataFinished)
            Toast.makeText(
                applicationContext,
                getString(R.string.user_creation_failed),
                Toast.LENGTH_LONG
            ).show()
        else {
            val service = retrofit.create(PostParticipantService::class.java)
            val call: Call<Participant> = service.sendParticipant(newParticipant)
            call.enqueue(object : Callback<Participant?> {
                override fun onResponse(
                    call: Call<Participant?>?,
                    response: Response<Participant?>
                ) {
                    if (response.isSuccessful) {
                        val prefs =
                            applicationContext.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        prefs.edit().putBoolean("isFirstRun", false).apply()
                        prefs.edit().putString("userId", response.body()?.id).apply()
                        db.participantDao().insert(response.body())
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.user_created),
                            Toast.LENGTH_LONG
                        ).show()
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    } else
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.creating_user_failed),
                            Toast.LENGTH_LONG
                        ).show()

                }

                override fun onFailure(call: Call<Participant?>, t: Throwable) {
                    Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG).show()
                    Toast.makeText(applicationContext, getString(R.string.creating_user_failed), Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun fetchAllMessages() {
        var count: Int
        val service: GetParticipantService = retrofit.create(GetParticipantService::class.java)
        val countCall: Call<Int> = service.fetchParticipantCount()
        countCall.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    count = response.body()!!
                    fetchAllMessagesPages(count/30)
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
            }
        })
    }

    private fun fetchAllMessagesPages(i: Int) {
        val db =
            application.let {
                Room.databaseBuilder(it, AppDatabase::class.java, MainActivity.DATABASE_NAME)
                    .allowMainThreadQueries().build()
            }
        val service: GetMessageService = retrofit.create(GetMessageService::class.java)
        for (page in 0..i) {
            val call: Call<List<Message>> = service.getMessagesForParticipant(MainActivity.PARTICIPANT_ID,page)
            call.enqueue(object : Callback<List<Message>> {
                override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                    if (response.isSuccessful) {
                        val list = response.body()!!
                        for (entry in list)
                            db.messageDao().insert(entry)
                    }
                    if(page == i)
                        isFetchDataFinished = true
                }

                override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                }
            })
        }
    }

}
