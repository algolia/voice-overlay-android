package com.algolia.instantsearch.voice.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.algolia.instantsearch.voice.VoiceSpeechRecognizer
import com.algolia.instantsearch.voice.demo.databinding.MainBinding
import com.algolia.instantsearch.voice.ui.Voice
import com.algolia.instantsearch.voice.ui.Voice.isRecordAudioPermissionGranted
import com.algolia.instantsearch.voice.ui.Voice.shouldExplainPermission
import com.algolia.instantsearch.voice.ui.Voice.showPermissionRationale
import com.algolia.instantsearch.voice.ui.VoiceInputDialogFragment
import com.algolia.instantsearch.voice.ui.VoicePermissionDialogFragment

class MainActivity : AppCompatActivity(), VoiceSpeechRecognizer.ResultsListener {

    private enum class Tag {
        Permission,
        Voice
    }

    private lateinit var binding: MainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonVoice.setOnClickListener { _ ->
            if (!isRecordAudioPermissionGranted()) {
                VoicePermissionDialogFragment().show(supportFragmentManager, Tag.Permission.name)
            } else {
                showVoiceDialog()
            }
        }

        binding.buttonPermission.setOnClickListener {
            VoicePermissionDialogFragment().show(supportFragmentManager, Tag.Permission.name)
        }
    }

    override fun onResults(possibleTexts: Array<out String>) {
        binding.results.text = possibleTexts.firstOrNull()?.capitalize()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Voice.isRecordPermissionWithResults(requestCode, grantResults)) {
            when {
                Voice.isPermissionGranted(grantResults) -> showVoiceDialog()
                shouldExplainPermission() -> showPermissionRationale(getPermissionView())
                else -> Voice.showPermissionManualInstructions(getPermissionView())
            }
        }
    }

    private fun showVoiceDialog() {
        getPermissionDialog()?.dismiss()
        (getVoiceDialog() ?: VoiceInputDialogFragment()).let {
            it.setSuggestions(
                "Phone case",
                "Running shoes"
            )
            it.show(supportFragmentManager, Tag.Voice.name)
        }
    }

    private fun getVoiceDialog() =
        (supportFragmentManager.findFragmentByTag(Tag.Voice.name) as? VoiceInputDialogFragment)

    private fun getPermissionDialog() =
        (supportFragmentManager.findFragmentByTag(Tag.Permission.name) as? VoicePermissionDialogFragment)

    private fun getPermissionView(): View =
        getPermissionDialog()!!.view!!.findViewById(R.id.positive)
}
