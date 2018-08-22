package com.algolia.instantsearch.voice.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import com.algolia.instantsearch.voice.ERROR_NO_LISTENER
import com.algolia.instantsearch.voice.R
import com.algolia.instantsearch.voice.VoiceInput
import kotlinx.android.synthetic.main.layout_voice_overlay.view.*

@SuppressLint("InflateParams")
class VoiceDialogFragment : DialogFragment(), VoiceInput.VoiceInputPresenter {

    private val content: View by lazy {
        LayoutInflater.from(activity).inflate(R.layout.layout_voice_overlay, null)
    }
    private var suggestions: List<String> = emptyList()

    val input: VoiceInput by lazy { VoiceInput(this) }

    fun setSuggestions(vararg suggestions: String) {
        this.suggestions = listOf(*suggestions)
    }

    //region Lifecycle
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        with(content) {
            closeButton.setOnClickListener { dismiss() }
            micButton.setOnClickListener { input.toggleVoiceRecognition() }
        }
        return AlertDialog.Builder(activity).setView(content).create()
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
        with(content) {
            micButton.toggleState()
            title.setText(if (isListening) R.string.voice_search_listening else R.string.voice_search_paused)
            if (isListening) ripple.start() else ripple.cancel()
            if (suggestions.isEmpty()) {
                hint.visibility = View.GONE
            } else {
                hint.visibility = View.VISIBLE
                with(content) {
                    suggestionText.text = suggestions.fold("") { acc, it -> "$acc$SEPARATOR$it\n" }
                }
            }
        }
    }

    override fun displayResult(text: CharSequence?, isError: Boolean) {
        with(content) {
            title.setText(if (isError) R.string.voice_search_error else R.string.voice_search_listening)
            hint.visibility = View.GONE
            suggestionText.text = text
            suggestionText.setTypeface(null, if (isError) Typeface.BOLD else Typeface.ITALIC)
        }
    }
    // endregion
}
