package at.fhooe.mc.messenger.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.mc.messenger.R
import at.fhooe.mc.messenger.model.Message

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages: List<Message> = listOf()

    companion object {
        var clickListener: ClickListener? = null
    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        Companion.clickListener = clickListener
    }


    interface ClickListener {
        fun onItemClick(position: Int, view: View);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false) as View
        return MessageViewHolder(itemView)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.messageContent.text = messages[position].content
    }


    fun setMessages(messages: List<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val messageContent: TextView = this.itemView.findViewById(R.id.message_textView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v != null) {
                clickListener!!.onItemClick(adapterPosition, v)
            }
        }
    }


}

