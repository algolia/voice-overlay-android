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
    private var suggestions: Array<String?>? = null
    private var listening = false

    private lateinit var speechRecognizer: SpeechRecognizer
    var voiceResultsListener: VoiceResultsListener? = null

    private val content: View by lazy {
        LayoutInflater.from(activity).inflate(R.layout.layout_voice_overlay, null)
    }

    //region Lifecycle
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        with (content) {
            closeButton.setOnClickListener { dismiss() }
            micButton.setOnClickListener {
                if (listening) stopVoiceRecognition() else startVoiceRecognition()
            }
            ripple.setOnClickListener { micButton.performClick() }
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
        listening = true
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)!!
        speechRecognizer.setRecognitionListener(this)
        speechRecognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)) //TODO: Consider using several results

        with(content) {
            ripple.start()
            micButton.toggleState()
            title.setText(R.string.voice_search_title)
            hint.visibility = View.VISIBLE
            updateSuggestions()
        }
    }

    private fun stopVoiceRecognition() {
        listening = false
        speechRecognizer.stopListening()
        speechRecognizer.destroy()

        with(content) {
            ripple.cancel()
            micButton.toggleState()
        }
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

        with(content) {
            title.setText(R.string.voice_search_error)
            hint.visibility = View.GONE
            suggestionText.text = errorText
            suggestionText.setTypeface(null, Typeface.BOLD)
        }
    }

    override fun onResults(results: Bundle) {
        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val matchesString = buildMatchesString(matches)
        Log.d(TAG, "onResults:" + matches!!.size + ": " + matchesString)

        stopVoiceRecognition()
        if (voiceResultsListener != null) {
            voiceResultsListener!!.onVoiceResults(matches)
        }
        dismiss()
    }

    override fun onPartialResults(partialResults: Bundle) {
        val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val matchesString = buildMatchesString(matches)

        with(content) {
            hint.visibility = View.GONE
            suggestionText.text = matchesString
            suggestionText.setTypeface(null, Typeface.ITALIC)
        }
        Log.d(TAG, "onPartialResults:" + matches!!.size + ": " + matchesString)
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        Log.d(TAG, "onEvent")
    }

    // endregion

    //endregion
    //region Helpers

    fun setSuggestions(vararg suggestions: String) {
        this.suggestions = arrayOfNulls(suggestions.size)
        for (i in suggestions.indices) {
            this.suggestions!![i] = suggestions[i]
        }
        if (view != null) {
            updateSuggestions()
        }
    }

    private fun updateSuggestions() {
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
