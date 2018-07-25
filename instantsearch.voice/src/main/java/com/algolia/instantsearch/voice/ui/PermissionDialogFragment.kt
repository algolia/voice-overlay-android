package com.algolia.instantsearch.voice.ui

import android.Manifest.permission.RECORD_AUDIO
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment

class PermissionDialogFragment : DialogFragment() {
    var title = DEFAULT_TITLE
    var message = DEFAULT_MESSAGE
    var positiveButton = DEFAULT_POSITIVE_BUTTON
    var negativeButton = DEFAULT_NEGATIVE_BUTTON

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity ?: throw IllegalStateException(ERROR_NO_ACTIVITY)
        return AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton) { _, _ ->
                    ActivityCompat.requestPermissions(activity, arrayOf(RECORD_AUDIO), ID_REQ_VOICE_PERM)
                    dismiss()
                }
                .setNegativeButton(negativeButton) { dialog, _ -> dialog.cancel() }
                .create()
    }

    companion object {
        const val ID_REQ_VOICE_PERM = 1
        const val DEFAULT_TITLE = "You can use voice search to find results"
        const val DEFAULT_MESSAGE = "Can we access your device's microphone to enable voice search?"
        const val DEFAULT_POSITIVE_BUTTON = "Allow microphone access"
        const val DEFAULT_NEGATIVE_BUTTON = "No"
        const val ERROR_NO_ACTIVITY = "PermissionDialogFragment must be used within an activity."
    }
}
