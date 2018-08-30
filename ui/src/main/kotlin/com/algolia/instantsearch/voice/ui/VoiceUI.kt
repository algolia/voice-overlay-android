package com.algolia.instantsearch.voice.ui

import android.view.View


interface VoiceUI {

    val formatterSuggestion: (String) -> String

    enum class Title(val resource: Int) {
        Search(R.string.searching_for),
        Listen(R.string.listening),
        Error(R.string.sorry_we_did_not_quite_get_that)
    }

    enum class Subtitle(val resource: Int) {
        Error(R.string.try_repeating_your_request),
        Listen(R.string.say_something_like)
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