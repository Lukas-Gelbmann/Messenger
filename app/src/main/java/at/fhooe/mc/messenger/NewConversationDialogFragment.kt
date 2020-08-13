package at.fhooe.mc.messenger

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment

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
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            val mView = inflater.inflate(R.layout.new_conversation_dialog, null)
            val editText = mView?.findViewById<EditText>(R.id.conversation_topic)
            builder.setView(mView)
                .setPositiveButton(getString(R.string.create), DialogInterface.OnClickListener { _, _ ->
                    if (!editText?.text.isNullOrEmpty())
                        listener.onDialogPositiveClick(this, editText?.text.toString())
                })
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}