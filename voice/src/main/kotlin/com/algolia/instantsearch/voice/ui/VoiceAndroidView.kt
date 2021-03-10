package com.algolia.instantsearch.voice.ui

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.algolia.instantsearch.voice.R

class VoiceAndroidView(
    private val view: ConstraintLayout
) : VoiceUI {

    private val context = view.context
    private val voiceInput: ConstraintLayout get() = view.findViewById(R.id.voiceInput)
    private val suggestionsView: TextView? get() = view.findViewById(R.id.voiceInput)
    private val View.close: ImageView get() = findViewById(R.id.close)
    private val View.microphone: VoiceMicrophone get() = findViewById(R.id.microphone)
    private val View.title: TextView get() = findViewById(R.id.microphone)
    private val View.subtitle: TextView get() = findViewById(R.id.subtitle)
    private val View.suggestions: TextView? get() = findViewById(R.id.suggestions)
    private val View.hint: TextView get() = findViewById(R.id.hint)
    private val View.ripple: RippleView get() = findViewById(R.id.ripple)

    override val formatterSuggestion = { suggestion: String ->
        context.getString(
            R.string.format_voice_suggestion_html,
            suggestion
        )
    }

    override fun setOnClickListenerClose(onClickListener: View.OnClickListener) {
        voiceInput.close.setOnClickListener(onClickListener)
    }

    override fun setOnClickMicrophoneListener(onClickListener: View.OnClickListener) {
        voiceInput.microphone.setOnClickListener(onClickListener)
    }

    override fun setSuggestions(suggestions: Array<out String>) {
        suggestionsView?.let {
            val formattedSuggestions = suggestions.joinToString("") { formatterSuggestion(it) }
            val html = HtmlCompat.fromHtml(formattedSuggestions, HtmlCompat.FROM_HTML_MODE_LEGACY)
            it.text = html
        }
    }

    override fun setTitle(title: VoiceUI.Title) {
        voiceInput.title.setText(title.resource)
    }

    override fun setSubtitle(subtitle: String) {
        voiceInput.subtitle.text = subtitle
    }

    override fun setSubtitle(subtitle: VoiceUI.Subtitle) {
        voiceInput.subtitle.setText(subtitle.resource)
    }

    override fun setSuggestionVisibility(isVisible: Boolean) {
        voiceInput.suggestions?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun setHintVisibility(isVisible: Boolean) {
        voiceInput.hint.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun setRippleActivity(isActive: Boolean) =
        if (isActive) voiceInput.ripple.start() else voiceInput.ripple.cancel()

    override fun setMicrophoneState(state: VoiceMicrophone.State) {
        voiceInput.microphone.state = state
    }

    override fun getMicrophoneState(): VoiceMicrophone.State = voiceInput.microphone.state
}
