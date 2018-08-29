package com.algolia.instantsearch.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.*

const val ERROR_NO_LISTENER = "The VoiceDialogFragment needs a VoiceResultsListener."

class VoiceInput @JvmOverloads constructor(
    private val presenter: VoiceInputPresenter,
    var listener: VoiceResultsListener? = null
) : RecognitionListener {

    /** a VoiceInput can either be **`Listening`** for input, **`Paused`** until further notice,
     *  displaying **`PartialResults`** or an **`Error`**. */
    enum class State {
        Listening,
        Paused,
        PartialResults,
        Error
    }

    /** Optional IETF language tag (as defined by BCP 47), for example "en-US", forwarded to the [SpeechRecognizer]. */
    var language: String? = null
    /** Maximum number of voice recognition matches to return. Defaults to 1. */
    var maxResults: Int = 1
    /** Current [state][State] of the VoiceInput.*/
    var state = State.Listening
        private set
    private lateinit var speechRecognizer: SpeechRecognizer

    //region Voice Recognition
    fun startVoiceRecognition() {
        state = State.Listening
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(presenter.getContext())!!
        speechRecognizer.setRecognitionListener(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults)
        language.let {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        }
        speechRecognizer.startListening(intent)

        updateUI()
    }

    fun stopVoiceRecognition() {
        state = State.Paused
        speechRecognizer.stopListening()
        speechRecognizer.destroy()
        updateUI()
    }

    fun toggleVoiceRecognition() {
        when (state) {
            State.Listening, State.PartialResults -> stopVoiceRecognition()
            State.Error, State.Paused -> startVoiceRecognition()
        }
    }

    // region RecognitionListener
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
        Log.d(TAG, "onResults:${matches!!.size}: $matchesString")

        stopVoiceRecognition()
        presenter.dismiss()
        if (listener == null) throw IllegalStateException(ERROR_NO_LISTENER)
        listener?.onVoiceResults(matches)
    }

    override fun onPartialResults(partialResults: Bundle) {
        state = State.PartialResults
        val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val matchesString = buildMatchesString(matches)
        updateUI(matchesString)
        Log.d(TAG, "onPartialResults:${matches!!.size}: $matchesString")
    }

    // region Unused RecognitionListener methods
    override fun onReadyForSpeech(params: Bundle) = Unit

    override fun onBeginningOfSpeech() = Unit

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray) = Unit

    override fun onEndOfSpeech() = Unit

    override fun onEvent(eventType: Int, params: Bundle) = Unit

    // endregion
    // endregion
    // endregion
    // region Helpers
    private fun buildMatchesString(matches: ArrayList<String>?): String? =
        matches?.fold("") { acc, it -> "$acc$it\n" }

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

    private fun updateUI(message: String? = null) {
        when (state) {
            State.Listening -> presenter.displayListening(true)
            State.Paused -> presenter.displayListening(false)
            State.PartialResults -> presenter.displayResult(message, isError = false)
            State.Error -> presenter.displayResult(message, isError = true)
        }
    }

    companion object {

        const val TAG = "VoiceInput"
    }

    // endregion
    // region Interfaces
    interface VoiceInputPresenter {

        fun displayListening(isListening: Boolean)
        fun displayResult(text: CharSequence?, isError: Boolean)
        fun dismiss()
        fun getContext(): Context?
    }

    interface VoiceResultsListener {

        fun onVoiceResults(matches: List<String>)
    }
    //endregion
}