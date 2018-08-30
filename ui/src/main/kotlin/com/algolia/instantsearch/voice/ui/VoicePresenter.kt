package com.algolia.instantsearch.voice.ui

import android.os.Bundle
import android.speech.RecognitionListener
import com.algolia.instantsearch.voice.VoiceSpeechRecognizer
import com.algolia.instantsearch.voice.resultsRecognition


class VoicePresenter(
    private val ui: VoiceUI,
    private val onResults: (results: ArrayList<String>) -> Unit
) : RecognitionListener,
    VoiceSpeechRecognizer.Listener {

    override fun onResults(results: Bundle) {
        onResults(results.resultsRecognition)
    }

    override fun onPartialResults(partialResults: Bundle) {
        val matches = partialResults.resultsRecognition

        ui.setSuggestionVisibility(isVisible = false)
        ui.setSubtitle(matches.joinToString("").capitalize())
        ui.setTitle(VoiceUI.Title.Listen)
    }

    override fun isListening(isListening: Boolean) {
        if (isListening) {
            ui.setHintVisibility(isVisible = false)
            ui.setRippleActivity(isActive = true)
            ui.setSuggestionVisibility(isVisible = true)
            ui.setTitle(VoiceUI.Title.Listen)
            ui.setSubtitle(VoiceUI.Subtitle.Listen)
            ui.setMicrophoneState(VoiceMicrophone.State.Activated)
        } else {
            ui.setHintVisibility(isVisible = true)
            ui.setRippleActivity(isActive = false)
            ui.setMicrophoneState(VoiceMicrophone.State.Deactivated)
        }
    }

    override fun onError(error: Int) {
        ui.setHintVisibility(isVisible = true)
        ui.setRippleActivity(isActive = false)
        ui.setSuggestionVisibility(isVisible = false)
        ui.setMicrophoneState(VoiceMicrophone.State.Deactivated)
        ui.setTitle(VoiceUI.Title.Error)
        ui.setSubtitle(VoiceUI.Subtitle.Error)
    }

    override fun onReadyForSpeech(params: Bundle) = Unit

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray) = Unit

    override fun onEvent(eventType: Int, params: Bundle) = Unit

    override fun onBeginningOfSpeech() = Unit

    override fun onEndOfSpeech() = Unit
}