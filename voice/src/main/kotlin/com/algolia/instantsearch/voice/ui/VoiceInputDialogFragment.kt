package com.algolia.instantsearch.voice.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.voice.R
import com.algolia.instantsearch.voice.VoiceSpeechRecognizer
import kotlinx.android.synthetic.main.voice_input.*


class VoiceInputDialogFragment : DialogFragment() {

    private enum class Field {
        Suggestions,
        AutoStart
    }

    private lateinit var speechRecognizer: VoiceSpeechRecognizer

    /** suggestions to display to the user before they speak. */
    var suggestions: Array<out String>? = null
        @JvmName("setSuggestionsArray")
        set(value) {
            if (arguments == null) {
                arguments = Bundle()
            }
            arguments?.putStringArray(Field.Suggestions.name, value)
            field = value
        }

    /** set to `false` if you want to manually [start] the voice recognition. */
    var autoStart: Boolean = true
        set(value) {
            if (arguments == null) arguments = Bundle()
            arguments?.putBoolean(Field.AutoStart.name, value)
            field = value
        }

    /** Sets [suggestions] to display to the user before they speak. */
    @Suppress("unused") // Java DX: Expose vararg setter
    fun setSuggestions(vararg suggestions: String) {
        this.suggestions = suggestions
    }

    /** Starts listening to user input, in case you disabled [autoStart]. */
    fun start() = speechRecognizer.start()

    // region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.VoiceDialogTheme)
        speechRecognizer = VoiceSpeechRecognizer(requireContext())
        suggestions = arguments?.getStringArray(Field.Suggestions.name)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.voice_input, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val androidView = VoiceAndroidView(voiceInput)
        val presenter = VoicePresenter(androidView) { result ->
            (activity as? VoiceSpeechRecognizer.ResultsListener)?.onResults(result.toTypedArray())
            dismiss()
        }

        androidView.setOnClickListenerClose(View.OnClickListener { dismiss() })
        androidView.setOnClickMicrophoneListener(View.OnClickListener {
            when (androidView.getMicrophoneState()) {
                VoiceMicrophone.State.Activated -> speechRecognizer.stop()
                VoiceMicrophone.State.Deactivated -> speechRecognizer.start()
            }
        })
        suggestions?.let {
            androidView.setSuggestions(it)
            androidView.setSubtitle(resources.getString(R.string.input_subtitle_listening))
        }
        speechRecognizer.setRecognitionListener(presenter)
        speechRecognizer.stateListener = presenter
    }

    override fun onResume() {
        super.onResume()
        if (autoStart) start()
    }

    override fun onPause() {
        super.onPause()
        if (!isRemoving) speechRecognizer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
    //endregion
}