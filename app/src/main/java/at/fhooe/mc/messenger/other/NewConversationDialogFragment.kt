package at.fhooe.mc.messenger.other

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import at.fhooe.mc.messenger.R

class NewConversationDialogFragment : DialogFragment() {
    private lateinit var listener: NewConversationDialogListener

    interface NewConversationDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, topic: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NewConversationDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement NoticeDialogListener")
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.new_conversation_dialog, null)
            val editText = view?.findViewById<EditText>(R.id.conversation_topic)
            builder.setView(view)
                .setPositiveButton(getString(R.string.create)) { _, _ ->
                    if (!editText?.text.isNullOrEmpty())
                        listener.onDialogPositiveClick(this, editText?.text.toString())
                }
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}