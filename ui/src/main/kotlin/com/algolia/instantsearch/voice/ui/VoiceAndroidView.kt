package com.algolia.instantsearch.voice.ui

import android.os.Build
import android.support.constraint.ConstraintLayout
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.voice.view.*


class VoiceAndroidView(
    private val view: ConstraintLayout
) : VoiceUI {

    private val context = view.context

    override val formatterSuggestion = { suggestion: String -> context.getString(R.string.voice_suggestion_html_format, suggestion) }

    override fun setOnClickListenerClose(onClickListener: View.OnClickListener) {
        view.voiceClose.setOnClickListener(onClickListener)
    }

    override fun setOnClickMicrophoneListener(onClickListener: View.OnClickListener) {
        view.voiceMicrophone.setOnClickListener(onClickListener)
    }

    override fun setSuggestions(suggestions: Array<out String>) {
        val formattedSuggestions = suggestions.joinToString("") { formatterSuggestion(it) }
        val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(formattedSuggestions, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(formattedSuggestions)
        }

        view.voiceSuggestions.text = html
    }

    override fun setTitle(title: VoiceUI.Title) {
        view.voiceTitle.setText(title.resource)
    }

    override fun setSubtitle(subtitle: String) {
        view.voiceSubtitle.text = subtitle
    }

    override fun setSubtitle(subtitle: VoiceUI.Subtitle) {
        view.voiceSubtitle.setText(subtitle.resource)
    }

    override fun setSuggestionVisibility(isVisible: Boolean) {
        view.voiceSuggestions.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun setHintVisibility(isVisible: Boolean) {
        view.voiceHint.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun setRippleActivity(isActive: Boolean) {
        if (isActive) view.voiceRipple.start() else view.voiceRipple.cancel()
    }

    override fun setMicrophoneState(state: VoiceMicrophone.State) {
        view.voiceMicrophone.state = state
    }

    override fun getMicrophoneState(): VoiceMicrophone.State = view.voiceMicrophone.state
}