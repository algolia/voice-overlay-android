package com.algolia.instantsearch.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer


class VoiceSpeechRecognizer(
    context: Context,
    private val maxResults: Int = 1,
    private val language: String? = null
) {

    interface StateListener {

        fun isListening(isListening: Boolean)
    }

    interface ResultsListener {

        //TODO: Document
        //TODO: Also see if can be vararg
        fun onResults(possibleTexts: Array<out String>)
    }

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also { intent ->
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults)
        language?.let {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        }
    }

    var stateListener: StateListener? = null

    fun setRecognitionListener(recognitionListener: RecognitionListener) {
        speechRecognizer.setRecognitionListener(recognitionListener)
    }

    fun start() {
        stateListener?.isListening(true)
        speechRecognizer.startListening(intent)
    }

    fun stop() {
        stateListener?.isListening(false)
        speechRecognizer.stopListening()
    }

    fun destroy() {
        try {
            speechRecognizer.destroy()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}