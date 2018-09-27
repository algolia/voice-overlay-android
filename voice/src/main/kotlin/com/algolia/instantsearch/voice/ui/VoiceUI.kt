package com.algolia.instantsearch.voice.ui

import android.view.View
import com.algolia.instantsearch.voice.R


interface VoiceUI {

    val formatterSuggestion: (String) -> String

    enum class Title(val resource: Int) {
        Listen(R.string.input_title_listening),
        Error(R.string.input_title_error)
    }

    enum class Subtitle(val resource: Int) {
        Error(R.string.input_subtitle_error),
        Listen(R.string.input_subtitle_listening)
    }

    fun setOnClickListenerClose(onClickListener: View.OnClickListener)

    fun setOnClickMicrophoneListener(onClickListener: View.OnClickListener)

    fun setSuggestions(suggestions: Array<out String>)

    fun setTitle(title: Title)

    fun setSubtitle(subtitle: String)

    fun setSubtitle(subtitle: Subtitle)

    fun setSuggestionVisibility(isVisible: Boolean)

    fun setHintVisibility(isVisible: Boolean)

    fun setRippleActivity(isActive: Boolean)

    fun setMicrophoneState(state: VoiceMicrophone.State)

    fun getMicrophoneState(): VoiceMicrophone.State
}