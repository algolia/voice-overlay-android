package com.algolia.instantsearch.voice.ui

import android.Manifest.permission.RECORD_AUDIO
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment

class PermissionDialogFragment : DialogFragment() {

    private enum class Argument {
        Title,
        Message,
        PositiveButton,
        NegativeButton
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null) {
            arguments = buildArguments()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments!!
        val title = bundle.getString(Argument.Title.name)
        val message = bundle.getString(Argument.Message.name)
        val positiveButton = bundle.getString(Argument.PositiveButton.name)
        val negativeButton = bundle.getString(Argument.NegativeButton.name)

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ ->
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(RECORD_AUDIO), PermissionRequestRecordAudio)
                dismiss()
            }
            .setNegativeButton(negativeButton) { dialog, _ -> dialog.cancel() }
            .create()
    }

    companion object {

        const val PermissionRequestRecordAudio = 1

        @JvmOverloads
        fun buildArguments(
            title: String = "You can use voice search to find results",
            message: String = "Can we access your device's microphone to enable voice search?",
            positiveButton: String = "Allow microphone access",
            negativeButton: String = "No"
        ) = Bundle().also {
            it.putString(Argument.Title.name, title)
            it.putString(Argument.Message.name, message)
            it.putString(Argument.PositiveButton.name, positiveButton)
            it.putString(Argument.NegativeButton.name, negativeButton)
        }
    }
}
