package at.fhooe.mc.messenger.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.mc.messenger.R
import at.fhooe.mc.messenger.model.Participant
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ParticipantAdapter(private var context: Context?) : RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    private var participants: List<Participant> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.participant_item,
            parent,
            false
        ) as View
        return ParticipantViewHolder(itemView)
    }

    override fun getItemCount() = participants.size

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val name = participants[position].firstName + " " + participants[position].lastName
        holder.participantName.text = name
        context?.let {
            Glide.with(it).load(participants[position].avatar)
                .apply(RequestOptions().circleCrop())
                .into(holder.participantImage)
        }
    }

    fun setParticipants(participants: List<Participant>) {
        this.participants = participants
        notifyDataSetChanged()
    }

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantName: TextView = this.itemView.findViewById(R.id.participant_textView)
        val participantImage: ImageView = this.itemView.findViewById(R.id.participant_imageView)
    }
}