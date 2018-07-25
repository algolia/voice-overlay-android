package com.algolia.instantsearch.voice.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.algolia.instantsearch.voice.R
import com.algolia.instantsearch.voice.ui.view.RippleView

import java.util.ArrayList

class VoiceDialogFragment : DialogFragment(), RecognitionListener {
    private var suggestions: Array<String?>? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var voiceResultsListener: VoiceResultsListener? = null

    private var hintView: TextView? = null
    private var suggestionsView: TextView? = null
    private var titleView: TextView? = null

    private var listening = false
    private var ripple: RippleView? = null

    //region Lifecycle

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @SuppressLint("InflateParams")/* Dialog's root view does not exist yet*/ val content = LayoutInflater.from(activity).inflate(R.layout.layout_voice_overlay, null)

        initViews(content)
        setButtonsOnClickListeners(content)
        updateSuggestions()
        return AlertDialog.Builder(activity)
                .setView(content)
                .create()
    }

    override fun onPause() {
        super.onPause()
        stopVoiceRecognition()
    }

    override fun onResume() {
        super.onResume()
        startVoiceRecognition()
    }

    //region Lifecycle.Helpers
    private fun initViews(content: View) {
        hintView = content.findViewById(R.id.hint)
        suggestionsView = content.findViewById(R.id.suggestions)
        titleView = content.findViewById(R.id.title)
        ripple = content.findViewById(R.id.ripple)
    }

    private fun setButtonsOnClickListeners(content: View) {
        val voiceFAB = content.findViewById<VoiceFAB>(R.id.micButton)
        content.findViewById<Button>(R.id.closeButton).setOnClickListener { dismiss() }
        content.findViewById<FloatingActionButton>(R.id.micButton).setOnClickListener {
            voiceFAB.toggleState() 
            if (listening) {
                stopVoiceRecognition()
            } else {
                startVoiceRecognition()
            }
        }
    }
    //endregion
    //endregion

    //region Voice Recognition
    private fun startVoiceRecognition() {
        listening = true
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1) //TODO: Consider using several results
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
        speechRecognizer!!.setRecognitionListener(this)
        speechRecognizer!!.startListening(recognizerIntent)

        ripple!!.start()
        titleView!!.setText(R.string.voice_search_title)
        updateSuggestions()
    }

    private fun stopVoiceRecognition() {
        listening = false
        if (speechRecognizer != null) {
            speechRecognizer!!.stopListening()
            speechRecognizer!!.destroy()
        }

        ripple!!.cancel()
        hintView!!.visibility = View.VISIBLE
        updateSuggestions()
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
        listening = false
        //TODO: Change button state
    }

    override fun onError(error: Int) {
        val errorText = getErrorMessage(error)
        Log.d(TAG, "onError: $errorText")
        stopVoiceRecognition()
        titleView!!.setText(R.string.voice_search_error)
        hintView!!.visibility = View.GONE
        suggestionsView!!.text = errorText
        suggestionsView!!.setTypeface(null, Typeface.BOLD)
    }

    override fun onResults(results: Bundle) {
        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val matchesString = buildMatchesString(matches)

        suggestionsView!!.text = matchesString
        suggestionsView!!.setTypeface(null, Typeface.NORMAL)
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

        hintView!!.visibility = View.GONE
        suggestionsView!!.text = matchesString
        suggestionsView!!.setTypeface(null, Typeface.ITALIC)
        Log.d(TAG, "onPartialResults:" + matches!!.size + ": " + matchesString)
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        Log.d(TAG, "onEvent")
    }

    private fun getErrorMessage(error: Int): String {
        var errorText = "Unknown error."
        when (error) {
            SpeechRecognizer.ERROR_AUDIO -> errorText = "Audio recording error."
            SpeechRecognizer.ERROR_CLIENT -> errorText = "Other client side errors."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> errorText = "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> errorText = "Other network related errors."
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> errorText = "Network operation timed out."
            SpeechRecognizer.ERROR_NO_MATCH -> errorText = "No recognition result matched."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> errorText = "RecognitionService busy."
            SpeechRecognizer.ERROR_SERVER -> errorText = "Server sends error status."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> errorText = "No speech input."
        }
        return errorText
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
        val context = context
        if (context != null) { // Ensure Fragment still attached
            hintView!!.visibility = View.VISIBLE
            val b = StringBuilder()
            if (suggestions != null && suggestions!!.size > 0) {
                for (s in suggestions!!) {
                    b.append(SEPARATOR).append(s).append("\n")
                }
            }
            suggestionsView!!.text = b.toString()
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

    fun setVoiceResultsListener(voiceResultsListener: VoiceResultsListener) {
        this.voiceResultsListener = voiceResultsListener
    }

    interface VoiceResultsListener {

        fun onVoiceResults(matches: ArrayList<String>)
    }

    companion object {
        const val SEPARATOR = "• "
        const val TAG = "VoiceDialogFragment"
    }
    //endregion
}
