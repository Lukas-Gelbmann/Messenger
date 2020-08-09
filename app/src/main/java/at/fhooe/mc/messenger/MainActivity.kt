package at.fhooe.mc.messenger

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import at.fhooe.mc.messenger.model.Conversation
import at.fhooe.mc.messenger.model.PostConversationService
import at.fhooe.mc.messenger.view.ConversationFragment
import at.fhooe.mc.messenger.view.ParticipantFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(),
    NewConversationDialogFragment.NewConversationDialogListener {

    companion object {
        const val serverIp = "http://192.168.1.191:8080/"
        //"http://10.0.0.29:8080/"
    }

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0)
            initComponents()
    }

    private fun initComponents() {
        if (savedInstanceState == null) {
            val fragment = ConversationFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                .commit()
        }
        val id = this.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("userId", "0")
        this.title = id

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
        var service: PostConversationService? = null
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(serverIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            service = retrofit.create(PostConversationService::class.java)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
        }

        val conversation = Conversation(topic)
        val call: Call<Conversation> = service!!.sendConversation(conversation)
        call.enqueue(object : Callback<Conversation?> {
            override fun onResponse(
                call: Call<Conversation?>?,
                response: Response<Conversation?>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "conversation created", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<Conversation?>, t: Throwable) {
                Toast.makeText(applicationContext, "sending data failed", Toast.LENGTH_LONG).show()
            }
        })

    }
}