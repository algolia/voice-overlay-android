package com.algolia.instantsearch.voice.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.algolia.instantsearch.voice.ui.Voice
import com.algolia.instantsearch.voice.VoiceSpeechRecognizer
import com.algolia.instantsearch.voice.ui.VoiceInputDialogFragment
import com.algolia.instantsearch.voice.ui.VoicePermissionDialogFragment
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.main.view.*


class MainActivity : AppCompatActivity(), VoiceSpeechRecognizer.ResultsListener {

    private enum class Tag {
        Permission,
        Voice
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        main.buttonVoice.setOnClickListener { _ ->
            if (!Voice.isRecordAudioPermissionGranted(this)) {
                VoicePermissionDialogFragment().show(supportFragmentManager, Tag.Permission.name)
            } else {
                showVoiceDialog()
            }
        }

        main.buttonPermission.setOnClickListener {
            VoicePermissionDialogFragment().show(supportFragmentManager, Tag.Permission.name)
        }
    }

    override fun onResults(results: Array<out String>) {
        main.results.text = results.firstOrNull()?.capitalize()
    }

    private fun showVoiceDialog() {
        getPermissionDialog()?.dismiss()
        (getVoiceDialog() ?: VoiceInputDialogFragment()).let {
            it.setArguments(
                "Hey, I just met you",
                "And this is crazy",
                "But here's my number",
                "So call me maybe"
            )
            it.show(supportFragmentManager, Tag.Voice.name)
        }
    }

    private fun getVoiceDialog() = (supportFragmentManager.findFragmentByTag(Tag.Voice.name) as? VoiceInputDialogFragment)

    private fun getPermissionDialog() = (supportFragmentManager.findFragmentByTag(Tag.Permission.name) as? VoicePermissionDialogFragment)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Voice.isRecordPermissionWithResults(requestCode, grantResults)) {
            if (Voice.isPermissionGranted(grantResults)) { showVoiceDialog()
            }
        }
    }
}
