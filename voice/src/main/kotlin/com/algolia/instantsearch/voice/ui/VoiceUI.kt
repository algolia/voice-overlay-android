package com.algolia.instantsearch.voice.ui

import android.view.View
import com.algolia.instantsearch.voice.R


public interface VoiceUI {

    public val formatterSuggestion: (String) -> String

    public enum class Title(public val resource: Int) {
        Listen(R.string.input_title_listening),
        Error(R.string.input_title_error)
    }

    public enum class Subtitle(public val resource: Int) {
        Error(R.string.input_subtitle_error),
        Listen(R.string.input_subtitle_listening)
    }

    public fun setOnClickListenerClose(onClickListener: View.OnClickListener)

    public fun setOnClickMicrophoneListener(onClickListener: View.OnClickListener)

    public fun setSuggestions(suggestions: Array<out String>)

    public fun setTitle(title: Title)

    public fun setSubtitle(subtitle: String)

    public fun setSubtitle(subtitle: Subtitle)

    public fun setSuggestionVisibility(isVisible: Boolean)

    public fun setHintVisibility(isVisible: Boolean)

    public fun setRippleActivity(isActive: Boolean)

    public fun setMicrophoneState(state: VoiceMicrophone.State)

    public fun getMicrophoneState(): VoiceMicrophone.State
}