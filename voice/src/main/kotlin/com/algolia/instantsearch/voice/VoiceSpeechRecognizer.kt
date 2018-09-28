package com.algolia.instantsearch.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent


class VoiceSpeechRecognizer @JvmOverloads constructor(
    private val maxResults: Int = 1,
    private val language: String? = null,
    val speechRecognition: SpeechRecognition
) {

    constructor(context: Context) : this(speechRecognition = SpeechRecognitionAndroidSpeech(context))

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also { intent ->
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults)
        language?.let {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, it)
        }
    }

    var stateListener: SpeechRecognition.StateListener? = null

    fun start() {
        stateListener?.isListening(true)
        speechRecognition.startListening(intent)
    }

    fun stop() {
        stateListener?.isListening(false)
        speechRecognition.stopListening()
    }

    fun destroy() {
        speechRecognition.destroy()
    }
}