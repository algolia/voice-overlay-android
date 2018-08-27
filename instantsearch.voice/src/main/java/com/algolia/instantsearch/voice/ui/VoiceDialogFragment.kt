package com.algolia.instantsearch.voice.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.voice.ERROR_NO_LISTENER
import com.algolia.instantsearch.voice.R
import com.algolia.instantsearch.voice.VoiceInput
import kotlinx.android.synthetic.main.layout_voice_overlay.*

@SuppressLint("InflateParams")
class VoiceDialogFragment : DialogFragment(), VoiceInput.VoiceInputPresenter {

    val input: VoiceInput = VoiceInput(this)

    private var suggestions: List<String> = emptyList()

    fun setSuggestions(vararg suggestions: String) {
        this.suggestions = listOf(*suggestions)
    }

    //region Lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.layout_voice_overlay, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        closeButton.setOnClickListener { dismiss() }
        micButton.setOnClickListener { input.toggleVoiceRecognition() }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is VoiceInput.VoiceResultsListener) input.listener = context
        else if (input.listener == null) throw IllegalStateException(ERROR_NO_LISTENER)
    }

    override fun onPause() {//TODO: Refactor using LifecycleObserver
        super.onPause()
        input.stopVoiceRecognition()
    }

    override fun onResume() {
        super.onResume()
        input.startVoiceRecognition()
    }

    //endregion
    //region Helpers
    companion object {

        const val SEPARATOR = "• "
    }
    //endregion

    // region VoiceInputPresenter
    override fun displayListening(isListening: Boolean) {
        micButton.toggleState()
        title.setText(if (isListening) R.string.voice_search_listening else R.string.voice_search_paused)
        if (isListening) ripple.start() else ripple.cancel()
        if (suggestions.isEmpty()) {
            hint.visibility = View.GONE
        } else {
            hint.visibility = View.VISIBLE
            suggestionText.text = suggestions.fold("") { acc, it -> "$acc$SEPARATOR$it\n" }
        }
    }

    override fun displayResult(text: CharSequence?, isError: Boolean) {
        title.setText(if (isError) R.string.voice_search_error else R.string.voice_search_listening)
        hint.visibility = View.GONE
        suggestionText.text = text
        suggestionText.setTypeface(null, if (isError) Typeface.BOLD else Typeface.ITALIC)
    }
    // endregion
}
