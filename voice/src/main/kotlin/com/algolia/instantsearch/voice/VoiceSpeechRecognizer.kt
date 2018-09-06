package com.algolia.instantsearch.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer


class VoiceSpeechRecognizer(
    private val context: Context,
    private val maxResults: Int = 1,
    private val language: String? = null
) {

    interface Listener {

        fun isListening(isListening: Boolean)
    }

    interface Result {

        fun onResults(results: Array<out String>)
    }

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also { intent ->
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults)
        language?.let {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        }
    }

    var listener: Listener? = null

    fun setRecognitionListener(recognitionListener: RecognitionListener) {
        speechRecognizer.setRecognitionListener(recognitionListener)
    }

    fun start() {
        listener?.isListening(true)
        speechRecognizer.startListening(intent)
    }

    fun stop() {
        listener?.isListening(false)
        speechRecognizer.stopListening()
    }

    fun destroy() {
        speechRecognizer.destroy()
    }
}