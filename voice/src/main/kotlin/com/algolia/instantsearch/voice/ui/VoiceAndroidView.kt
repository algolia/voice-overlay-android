package com.algolia.instantsearch.voice.ui

import android.os.Build
import android.support.constraint.ConstraintLayout
import android.text.Html
import android.view.View
import com.algolia.instantsearch.voice.R
import kotlinx.android.synthetic.main.voice_input.view.*


class VoiceAndroidView(
    private val view: ConstraintLayout
) : VoiceUI {

    private val context = view.context

    override val formatterSuggestion = { suggestion: String -> context.getString(R.string.format_voice_suggestion_html, suggestion) }

    override fun setOnClickListenerClose(onClickListener: View.OnClickListener) {
        view.voiceInput.close.setOnClickListener(onClickListener)
    }

    override fun setOnClickMicrophoneListener(onClickListener: View.OnClickListener) {
        view.voiceInput.microphone.setOnClickListener(onClickListener)
    }

    override fun setSuggestions(suggestions: Array<out String>) {
        view.suggestions?.let {
            val formattedSuggestions = suggestions.joinToString("") { formatterSuggestion(it) }
            val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(formattedSuggestions, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(formattedSuggestions)
            }
            it.text = html
        }
    }

    override fun setTitle(title: VoiceUI.Title) {
        view.voiceInput.title.setText(title.resource)
    }

    override fun setSubtitle(subtitle: String) {
        view.voiceInput.subtitle.text = subtitle
    }

    override fun setSubtitle(subtitle: VoiceUI.Subtitle) {
        view.voiceInput.subtitle.setText(subtitle.resource)
    }

    override fun setSuggestionVisibility(isVisible: Boolean) {
        view.voiceInput.suggestions?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun setHintVisibility(isVisible: Boolean) {
        view.voiceInput.hint.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun setRippleActivity(isActive: Boolean) =
        if (isActive) view.voiceInput.ripple.start() else view.voiceInput.ripple.cancel()

    override fun setMicrophoneState(state: VoiceMicrophone.State) {
        view.voiceInput.microphone.state = state
    }

    override fun getMicrophoneState(): VoiceMicrophone.State = view.voiceInput.microphone.state
}