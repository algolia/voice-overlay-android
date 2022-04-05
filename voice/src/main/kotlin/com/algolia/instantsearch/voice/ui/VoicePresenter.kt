package com.algolia.instantsearch.voice.ui

import android.os.Bundle
import android.speech.RecognitionListener
import com.algolia.instantsearch.voice.VoiceSpeechRecognizer
import com.algolia.instantsearch.voice.resultsRecognition
import java.util.*

public class VoicePresenter(
    private val ui: VoiceUI,
    private val onResults: (results: ArrayList<String>) -> Unit
) : RecognitionListener,
    VoiceSpeechRecognizer.StateListener {

    override fun onResults(results: Bundle) {
        onResults(results.resultsRecognition)
    }

    private var hasPartialResults: Boolean = false

    override fun onPartialResults(partialResults: Bundle) {
        val matches = partialResults.resultsRecognition

        ui.setSuggestionVisibility(false)
        ui.setTitle(VoiceUI.Title.Listen)
        matches.firstOrNull()
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            ?.let(ui::setSubtitle)
        hasPartialResults = true
    }

    override fun isListening(isListening: Boolean) {
        if (isListening) {
            ui.setHintVisibility(false)
            ui.setRippleActivity(true)
            ui.setSuggestionVisibility(true)
            ui.setTitle(VoiceUI.Title.Listen)
            ui.setSubtitle(VoiceUI.Subtitle.Listen)
            ui.setMicrophoneState(VoiceMicrophone.State.Activated)
        } else {
            if (!hasPartialResults) {
                ui.setHintVisibility(true)
            }
            ui.setRippleActivity(false)
            ui.setMicrophoneState(VoiceMicrophone.State.Deactivated)
        }
    }

    override fun onError(error: Int) {
        ui.setHintVisibility(true)
        ui.setRippleActivity(false)
        ui.setSuggestionVisibility(false)
        ui.setMicrophoneState(VoiceMicrophone.State.Deactivated)
        ui.setTitle(VoiceUI.Title.Error)
        ui.setSubtitle(VoiceUI.Subtitle.Error)
        hasPartialResults = false
    }

    override fun onReadyForSpeech(params: Bundle): Unit = Unit

    override fun onRmsChanged(rmsdB: Float): Unit = Unit

    override fun onBufferReceived(buffer: ByteArray): Unit = Unit

    override fun onEvent(eventType: Int, params: Bundle): Unit = Unit

    override fun onBeginningOfSpeech(): Unit = Unit

    override fun onEndOfSpeech(): Unit = Unit
}