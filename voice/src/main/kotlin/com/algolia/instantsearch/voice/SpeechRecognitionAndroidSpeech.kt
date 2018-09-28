package com.algolia.instantsearch.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer


class SpeechRecognitionAndroidSpeech(context: Context) : SpeechRecognition {

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    override fun setRecognitionListener(recognitionListener: RecognitionListener) {
        speechRecognizer.setRecognitionListener(recognitionListener)

    }

    override fun startListening(intent: Intent) {
        speechRecognizer.startListening(intent)

    }

    override fun stopListening() {
        speechRecognizer.stopListening()
    }

    override fun destroy() {
        try {
            speechRecognizer.destroy()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}