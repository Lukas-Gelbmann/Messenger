package at.fhooe.mc.messenger

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.fhooe.mc.messenger.model.Participant
import at.fhooe.mc.messenger.model.PostParticipantService
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
    private var service: PostParticipantService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mFirstNameEditText = findViewById(R.id.text_firstname)
        mLastNameEditText = findViewById(R.id.text_lastname)
        mEmailEditText = findViewById(R.id.text_email)
        mCreateButton = findViewById(R.id.button_createuser)
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(MainActivity.serverIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            service = retrofit.create(PostParticipantService::class.java)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun onClick(view: View) {
        val participant = Participant(
            mFirstNameEditText?.text.toString(),
            mLastNameEditText?.text.toString(),
            mEmailEditText?.text.toString()
        )
        val call: Call<Participant> = service!!.sendParticipant(participant)
        call.enqueue(object : Callback<Participant?> {
            override fun onResponse(
                call: Call<Participant?>?,
                response: Response<Participant?>
            ) {
                if (response.isSuccessful) {
                    val prefs = applicationContext.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    prefs.edit().putBoolean("isFirstRun", false).apply()
                    prefs.edit().putString("userId", response.body()?.id).apply()
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                } else
                    Toast.makeText(
                        applicationContext,
                        "sending data failed",
                        Toast.LENGTH_LONG
                    ).show()

            }

            override fun onFailure(call: Call<Participant?>, t: Throwable) {
                Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG).show()
                Toast.makeText(applicationContext, "sending data failed", Toast.LENGTH_LONG).show()

            }
        })

    }
}
