package com.algolia.instantsearch.voice.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.voice.VoiceSpeechRecognizer
import kotlinx.android.synthetic.main.voice.*


class VoiceDialogFragment : DialogFragment() {

    private enum class Field {
        Suggestions
    }

    private lateinit var speechRecognizer: VoiceSpeechRecognizer

    private var suggestions: Array<out String>? = null

    fun setArguments(vararg suggestions: String) {
        arguments = Bundle().also {
            it.putStringArray(Field.Suggestions.name, suggestions)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.VoiceDialogTheme)
        speechRecognizer = VoiceSpeechRecognizer(requireContext())
        suggestions = arguments?.getStringArray(Field.Suggestions.name)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.voice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val androidView = VoiceAndroidView(voice)
        val presenter = VoicePresenter(androidView) { result ->
            (activity as? VoiceSpeechRecognizer.Result)?.onResults(result.toTypedArray())
            dismiss()
        }

        androidView.setOnClickListenerClose(View.OnClickListener { dismiss() })
        androidView.setOnClickMicrophoneListener(View.OnClickListener {
            when (androidView.getMicrophoneState()) {
                VoiceMicrophone.State.Activated -> speechRecognizer.stop()
                VoiceMicrophone.State.Deactivated -> speechRecognizer.start()
            }
        })
        suggestions?.let(androidView::setSuggestions)
        speechRecognizer.setRecognitionListener(presenter)
        speechRecognizer.listener = presenter
    }


    override fun onResume() {
        super.onResume()
        speechRecognizer.start()
    }

    override fun onPause() {
        super.onPause()
        if (!isRemoving) speechRecognizer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}