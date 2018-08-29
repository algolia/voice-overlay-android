package com.algolia.instantsearch.voice.demo

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.algolia.instantsearch.voice.Voice
import com.algolia.instantsearch.voice.ui.PermissionDialogFragment
import com.algolia.instantsearch.voice.ui.VoiceDialogFragment
import com.algolia.instantsearch.voice.VoiceInput
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), VoiceInput.VoiceResultsListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val voiceFragment = VoiceDialogFragment()

        button.setOnClickListener { _ ->
            if (!Voice.hasRecordPermission(this)) {
                PermissionDialogFragment().let {
                    it.arguments = PermissionDialogFragment.buildArguments(title = "Voice Search.")
                    it.show(supportFragmentManager, "perm")
                }
            } else {
                voiceFragment.setSuggestions("Something", "Something else")
                voiceFragment.input.language = "en-US"
                voiceFragment.input.maxResults = 2
                voiceFragment.show(supportFragmentManager, "voice")
            }
        }
    }

    override fun onVoiceResults(matches: List<String>) {
        textView.text = matches[0]
    }
}