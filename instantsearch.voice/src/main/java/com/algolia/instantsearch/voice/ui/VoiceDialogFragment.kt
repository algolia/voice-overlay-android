package com.algolia.instantsearch.voice.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.algolia.instantsearch.voice.R
import kotlinx.android.synthetic.main.layout_voice_overlay.view.*
import java.util.*

@SuppressLint("InflateParams")
class VoiceDialogFragment : DialogFragment(), RecognitionListener {
    enum class State {
        Listening,
        Paused,
        PartialResults,
        Error
    }

    private var state = State.Listening
    private var suggestions: Array<String?>? = null

    private lateinit var speechRecognizer: SpeechRecognizer
    var voiceResultsListener: VoiceResultsListener? = null

    private val content: View by lazy {
        LayoutInflater.from(activity).inflate(R.layout.layout_voice_overlay, null)
    }

    //region Lifecycle
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        with(content) {
            closeButton.setOnClickListener { dismiss() }
            micButton.setOnClickListener {
                when (state) {
                    State.Listening, State.PartialResults -> stopVoiceRecognition()
                    State.Error, State.Paused -> startVoiceRecognition()
                }
            }
            ripple.setOnClickListener { micButton.performClick() } //TODO : Needed?
        }
        return AlertDialog.Builder(activity)
                .setView(content).create()
    }

    override fun onPause() {
        super.onPause()
        stopVoiceRecognition()
    }

    override fun onResume() {
        super.onResume()
        startVoiceRecognition()
    }

    //endregion
    //region Voice Recognition
    private fun startVoiceRecognition() {
        state = State.Listening
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)!!
        speechRecognizer.setRecognitionListener(this)
        speechRecognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)) //TODO: Consider using several results

        updateUI()
    }

    private fun stopVoiceRecognition() {
        state = State.Paused
        speechRecognizer.stopListening()
        speechRecognizer.destroy()
        updateUI()
    }

    // region RecognitionListener
    override fun onReadyForSpeech(params: Bundle) {
        Log.d(TAG, "onReadyForSpeech:$params")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d(TAG, "onBeginningOfSpeech")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.d(TAG, "onBufferReceived: $buffer")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech")
    }

    override fun onError(error: Int) {
        val errorText = getErrorMessage(error)
        Log.d(TAG, "onError: $errorText")
        stopVoiceRecognition()
        state = State.Error
        updateUI(errorText)
    }

    override fun onResults(results: Bundle) {
        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val matchesString = buildMatchesString(matches)
        Log.d(TAG, "onResults:" + matches!!.size + ": " + matchesString)

        stopVoiceRecognition()
        if (voiceResultsListener != null) { //TODO: Directly throw if missing listener
            voiceResultsListener!!.onVoiceResults(matches)
        }
        dismiss()
    }

    override fun onPartialResults(partialResults: Bundle) {
        state = State.PartialResults
        val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val matchesString = buildMatchesString(matches)
        updateUI(matchesString)
        Log.d(TAG, "onPartialResults:" + matches!!.size + ": " + matchesString)
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        Log.d(TAG, "onEvent")
    }

    // endregion
    // endregion
    //region Helpers

    fun setSuggestions(vararg suggestions: String) {
        this.suggestions = arrayOfNulls(suggestions.size)
        for (i in suggestions.indices) {
            this.suggestions!![i] = suggestions[i]
        }
    }

    private fun updateUI(message: String? = null) {
        with(content) {
            when (state) {
                State.Listening -> displayListening(true)
                State.Paused -> displayListening(false)
                State.PartialResults -> displayResult(message, isError = false)
                State.Error -> displayResult(message, isError = true)
            }
        }
    }

    private fun View.displayListening(isListening: Boolean) {
        micButton.toggleState()
        title.setText(if (isListening) R.string.voice_search_listening else R.string.voice_search_paused)
        if (isListening) ripple.start() else ripple.cancel()
        hint.visibility = View.VISIBLE
        updateSuggestions()
    }

    private fun View.displayResult(message: String?, isError: Boolean) {
        title.setText(if (isError) R.string.voice_search_error else R.string.voice_search_listening)
        hint.visibility = View.GONE
        suggestionText.text = message
        suggestionText.setTypeface(null, if (isError) Typeface.BOLD else Typeface.ITALIC)
    }

    private fun updateSuggestions() { //TODO: inline and simplify
        val b = StringBuilder()
        if (suggestions != null && suggestions!!.isNotEmpty()) {
            for (s in suggestions!!) {
                b.append(SEPARATOR).append(s).append("\n")
            }
        }
        with(content) {
            suggestionText.text = b.toString()
        }
    }

    private fun buildMatchesString(matches: ArrayList<String>?): String {
        val b = StringBuilder()
        if (matches != null) {
            for (match in matches) {
                b.append(match).append("\n")
            }
        }
        return b.toString()
    }

    private fun getErrorMessage(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
        SpeechRecognizer.ERROR_CLIENT -> "Other client side errors."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
        SpeechRecognizer.ERROR_NETWORK -> "Other network related errors."
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network operation timed out."
        SpeechRecognizer.ERROR_NO_MATCH -> "No recognition result matched."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy."
        SpeechRecognizer.ERROR_SERVER -> "Server sends error status."
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input."
        else -> "Unknown error."
    }

    companion object {
        const val SEPARATOR = "• "
        const val TAG = "VoiceDialogFragment"
    }
    //endregion

    interface VoiceResultsListener {
        fun onVoiceResults(matches: ArrayList<String>)
    }
}
