package com.algolia.instantsearch.voice.ui

import android.view.View


interface VoiceUI {

    val formatterSuggestion: (String) -> String

    enum class Title(val resource: Int) {
        Search(R.string.voice_title_search),
        Listen(R.string.voice_title_listen),
        Error(R.string.voice_title_error)
    }

    enum class Subtitle(val resource: Int) {
        Error(R.string.voice_subtitle_error),
        Listen(R.string.voice_subtitle_listen)
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