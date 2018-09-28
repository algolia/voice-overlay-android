package com.algolia.instantsearch.voice

import android.content.Intent
import android.speech.RecognitionListener


interface SpeechRecognition {

    interface StateListener {

        fun isListening(isListening: Boolean)
    }

    interface ResultsListener {

        //TODO: Document
        //TODO: Also see if can be vararg
        fun onResults(possibleTexts: Array<out String>)
    }

    fun setRecognitionListener(recognitionListener: RecognitionListener)

    fun startListening(intent: Intent)

    fun stopListening()

    fun destroy()
}