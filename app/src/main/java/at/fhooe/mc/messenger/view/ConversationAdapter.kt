package at.fhooe.mc.messenger.view

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.mc.messenger.R
import at.fhooe.mc.messenger.model.Conversation


class ConversationAdapter : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {
    private var conversations: List<Conversation> = listOf()
    companion object {
        var clickListener: ClickListener? = null
    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        Companion.clickListener = clickListener
    }


    interface ClickListener {
        fun onItemClick(position: Int, view: View);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.conversation_item, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        return ConversationViewHolder(itemView)
    }

    override fun getItemCount() = conversations.size

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.conversationTopic.text = conversations[position].topic
    }


    fun setConversations(conversations: List<Conversation>){
        this.conversations = conversations
        notifyDataSetChanged()
    }

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val conversationTopic: TextView = this.itemView.findViewById(R.id.conversation_textview)

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

