package com.algolia.instantsearch.voice.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.algolia.instantsearch.voice.Voice
import com.algolia.instantsearch.voice.ui.PermissionDialogFragment
import com.algolia.instantsearch.voice.VoiceSpeechRecognizer
import com.algolia.instantsearch.voice.ui.VoiceDialogFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), VoiceSpeechRecognizer.Result {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val voiceFragment = VoiceDialogFragment()

        button.setOnClickListener { _ ->
            if (!Voice.isRecordAudioPermissionGranted(this)) {
                PermissionDialogFragment().let {
                    it.arguments = PermissionDialogFragment.buildArguments(title = "Voice Search.")
                    it.show(supportFragmentManager, "perm")
                }
            } else {
                voiceFragment.setArguments("Something", "Something else")
                voiceFragment.show(supportFragmentManager, "voice")
            }
        }
    }

    override fun onResults(results: Array<out String>) {
        textView.text = results[0]
    }
}
