package com.algolia.instantsearch.voice.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.algolia.instantsearch.voice.Voice
import com.algolia.instantsearch.voice.ui.PermissionDialogFragment
import com.algolia.instantsearch.voice.ui.VoiceDialogFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), VoiceDialogFragment.VoiceResultsListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            if (!Voice.hasRecordPermission(this)) {
                PermissionDialogFragment().show(supportFragmentManager, "perm")
            } else {
                val voiceFragment = VoiceDialogFragment() //FIXME: Handle orientation changes, storing state properly
                voiceFragment.voiceResultsListener = this
                voiceFragment.languageCode = "en-US"
                voiceFragment.show(supportFragmentManager, "voice")
            }
        }
    }

    override fun onVoiceResults(matches: List<String>) {
        textView.text = matches[0]
    }
}
