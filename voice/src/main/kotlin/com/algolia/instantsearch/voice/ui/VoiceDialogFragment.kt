package com.algolia.instantsearch.voice.ui

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.voice.ERROR_NO_LISTENER
import com.algolia.instantsearch.voice.R
import com.algolia.instantsearch.voice.VoiceInput
import kotlinx.android.synthetic.main.voice.*


class VoiceDialogFragment : DialogFragment(), VoiceInput.VoiceInputPresenter {

    private enum class Key {
        Suggestions
    }

    val input: VoiceInput = VoiceInput(this)

    private var suggestions: Array<out String> = arrayOf()

    fun setSuggestions(vararg suggestions: String) {
        this.suggestions = suggestions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.voice, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(Key.Suggestions.name, suggestions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        voiceClose.setOnClickListener { dismiss() }
        voiceMicrophone.setOnClickListener { input.toggleVoiceRecognition() }
        savedInstanceState?.let {
            suggestions = it.getStringArray(Key.Suggestions.name)!!
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is VoiceInput.VoiceResultsListener) input.listener = context
        else if (input.listener == null) throw IllegalStateException(ERROR_NO_LISTENER)
    }

    override fun onPause() {
        super.onPause()
        input.stopVoiceRecognition()
    }

    override fun onResume() {
        super.onResume()
        voiceMicrophone.state = VoiceFAB.State.Activated
        voiceTryAgain.visibility = View.INVISIBLE
        input.startVoiceRecognition()
    }

    override fun displayListening(isListening: Boolean) {
        voiceTryAgain.visibility = if (isListening) View.INVISIBLE else View.VISIBLE
        voiceMicrophone.state = if (isListening) VoiceFAB.State.Activated else VoiceFAB.State.Deactivated
        voiceTitle.setText(if (isListening) R.string.voice_search_listening else R.string.voice_search_paused)
        if (isListening) voiceRipple.start() else voiceRipple.cancel()
        if (suggestions.isEmpty()) {
            voiceHint.visibility = View.GONE
        } else {
            voiceHint.visibility = View.VISIBLE
            voiceSuggestionText.text = suggestions.joinToString("") {
                Html.fromHtml(getString(R.string.voice_suggestion_format, it))
            }
        }
    }

    override fun displayResult(text: CharSequence?, isError: Boolean) {
        voiceTryAgain.visibility = if (isError) View.VISIBLE else View.INVISIBLE
        voiceTitle.setText(if (isError) R.string.voice_search_error else R.string.voice_search_listening)
        voiceHint.visibility = View.GONE
        voiceSuggestionText.text = text
        voiceSuggestionText.setTypeface(null, if (isError) Typeface.BOLD else Typeface.ITALIC)
    }
}
