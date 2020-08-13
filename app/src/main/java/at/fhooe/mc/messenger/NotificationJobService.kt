package at.fhooe.mc.messenger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.room.Room
import at.fhooe.mc.messenger.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NotificationJobService : JobService() {

    var db: AppDatabase? = null
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.serverIp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        var mNotifyManager: NotificationManager? = null
    }


    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(MainActivity.TAG, "Running service now..")
        db = applicationContext.let {
            Room.databaseBuilder(it, AppDatabase::class.java, MainActivity.DATABASE_NAME)
                .allowMainThreadQueries().build()
        }

        createNotificationChannel()

        checkForNewMessages()

        scheduleRefresh()

        jobFinished(params, false)

        return true
    }

    private fun checkForNewMessages() {
        val messageService = retrofit.create(GetMessageService::class.java)
        val messagesCall: Call<Int> =
            messageService.getMessageCountForParticipant(MainActivity.PARTICIPANT_ID)
        messagesCall.enqueue(object : Callback<Int> {

            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (!response.isSuccessful)
                    return
                val serverCount = response.body()!!
                val localCount = db!!.messageDao().getMessageCount("1")
                Log.i(MainActivity.TAG, "message count: " + response.body()!!.toString())
                if (serverCount > localCount)
                    getNewMessages()
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.e(MainActivity.TAG, "fetching data failed")
            }
        })
    }

    private fun getNewMessages() {
        val messageService = retrofit.create(GetMessageService::class.java)
        val messagesCall: Call<List<Message>> =
            messageService.getNewestMessagesForParticipant(MainActivity.PARTICIPANT_ID)
        messagesCall.enqueue(object : Callback<List<Message>> {

            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                if (!response.isSuccessful)
                    return
                val newServerMessages = response.body()!!
                for(message in newServerMessages)
                    db!!.messageDao().insert(message)
                getParticipant(newServerMessages[0])

            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Log.e(MainActivity.TAG, "fetching data failed")
            }
        })
    }

    private fun getParticipant(message: Message) {
        val participantService = retrofit.create(GetParticipantService::class.java)
        val participantCall: Call<Participant>? =
            participantService.getParticipant(message.senderId) // TODO receiverId???
        participantCall!!.enqueue(object : Callback<Participant> {

            override fun onResponse(call: Call<Participant>, response: Response<Participant>) {
                if (!response.isSuccessful)
                    return
                createNotification(message, response.body())
            }

            override fun onFailure(call: Call<Participant>, t: Throwable) {
                Log.e(MainActivity.TAG, "fetching data failed")
            }

        })
    }

    private fun createNotification(message: Message, participant: Participant?) {
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder: NotificationCompat.Builder? =
            NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.new_message_from) + participant!!.firstName + " " + participant.lastName)
                .setContentText(message.content)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
        if (builder != null) {
            mNotifyManager!!.notify(0, builder.build())
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    private fun createNotificationChannel() {
        mNotifyManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            PRIMARY_CHANNEL_ID,
            "Notification_Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Notifications from messenger"
        mNotifyManager!!.createNotificationChannel(notificationChannel)
    }


    private fun scheduleRefresh() {
        val mJobScheduler = applicationContext
            .getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val mJobBuilder =
            JobInfo.Builder(1, ComponentName(packageName, NotificationJobService::class.java.name))
        mJobBuilder.setMinimumLatency(10000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        mJobScheduler.schedule(mJobBuilder.build())
    }


}