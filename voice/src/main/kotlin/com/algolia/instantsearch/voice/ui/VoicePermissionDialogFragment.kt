package com.algolia.instantsearch.voice.ui

import android.Manifest
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.algolia.instantsearch.voice.R

class VoicePermissionDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.VoiceDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.voice_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val voicePermission = view.findViewById<ConstraintLayout>(R.id.voicePermission)
        voicePermission.let {
            it.findViewById<ImageView>(R.id.close).setOnClickListener { dismiss() }
            it.findViewById<Button>(R.id.positive).setOnClickListener {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    Voice.PermissionRequestRecordAudio
                )
            }
            it.findViewById<Button>(R.id.negative).setOnClickListener { dismiss() }
        }
    }
}