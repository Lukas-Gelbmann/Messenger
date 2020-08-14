package at.fhooe.mc.messenger.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.mc.messenger.databinding.MessageItemBinding
import at.fhooe.mc.messenger.model.Message
import at.fhooe.mc.messenger.viewmodel.MessagingViewModel
import at.fhooe.mc.messenger.view.MessageAdapter.ViewHolder

class MessageAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var messages: List<Message> = listOf()
    var viewModel : MessagingViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MessageItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(messages[position])

    fun setMessages(messages: List<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.message = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

}

