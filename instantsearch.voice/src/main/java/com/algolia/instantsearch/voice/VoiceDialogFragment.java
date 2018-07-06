package com.algolia.instantsearch.voice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class VoiceDialogFragment extends DialogFragment implements RecognitionListener {
    public static final String SEPARATOR = "• ";
    public static final String TAG = "VoiceDialogFragment";

    private String[] suggestions;
    private SpeechRecognizer speechRecognizer;
    private VoiceResultsListener voiceResultsListener;

    private ProgressBar progressBar;
    private TextView hintView;
    private TextView suggestionsView;
    private TextView titleView;

    private boolean listening = false;

    //region Lifecycle

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams" /* Dialog's root view does not exist yet*/) final View content = LayoutInflater.from(getActivity()).inflate(R.layout.layout_voice_overlay, null);

        initViews(content);
        setButtonsOnClickListeners(content);
        updateSuggestions();
        return new AlertDialog.Builder(getActivity())
                .setView(content)
                .create();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopVoiceRecognition();
    }

    @Override
    public void onResume() {
        super.onResume();
        startVoiceRecognition();
    }

    //region Lifecycle.Helpers
    private void initViews(View content) {
        hintView = content.findViewById(R.id.hint);
        suggestionsView = content.findViewById(R.id.suggestions);
        titleView = content.findViewById(R.id.title);
        progressBar = content.findViewById(R.id.feedback);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            if (getContext().getTheme().resolveAttribute(R.attr.algolia_voiceOverlayFeedbackColor, typedValue, true)) {
                progressBar.getProgressDrawable().setColorFilter(typedValue.data, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private void setButtonsOnClickListeners(View content) {
        content.<Button>findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        content.<Button>findViewById(R.id.micButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listening) {
                    stopVoiceRecognition();
                } else {
                    startVoiceRecognition();
                }
            }
        });
    }
    //endregion
    //endregion

    //region Voice Recognition
    private void startVoiceRecognition() {
        listening = true;
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1); //TODO: Consider using several results

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        speechRecognizer.setRecognitionListener(this);
        speechRecognizer.startListening(recognizerIntent);

        updateSuggestions();
        titleView.setText(R.string.voice_search_title);
        //TODO: Change button state
    }

    private void stopVoiceRecognition() {
        listening = false;
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
        }
        hintView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        updateSuggestions();
    }

    // region RecognitionListener

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech:" + params);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress((int) rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
        listening = false;
        //TODO: Change button state
    }

    @Override
    public void onError(int error) {
        String errorText = getErrorMessage(error);
        Log.d(TAG, "onError: " + errorText);
        stopVoiceRecognition();
        titleView.setText(R.string.voice_search_error);
        hintView.setVisibility(View.GONE);
        suggestionsView.setText(errorText);
        suggestionsView.setTypeface(null, Typeface.BOLD);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        StringBuilder b = new StringBuilder();
        for (String match : matches) {
            b.append(match).append("\n");
        }
        suggestionsView.setText(b.toString());
        suggestionsView.setTypeface(null, Typeface.NORMAL);
        Log.d(TAG, "onResults:" + matches.size() + ": " + b.toString());
        stopVoiceRecognition();
        voiceResultsListener.onVoiceResults(matches);
        dismiss();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        StringBuilder b = new StringBuilder();
        for (String match : matches) {
            b.append(match).append("\n");
        }
        hintView.setVisibility(View.GONE);
        suggestionsView.setText(b.toString());
        suggestionsView.setTypeface(null, Typeface.ITALIC);
        Log.d(TAG, "onPartialResults:" + matches.size() + ": " + b.toString());
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent");
    }

    @NonNull
    private String getErrorMessage(int error) {
        String errorText = "Unknown error.";
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorText = "Audio recording error.";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorText = "Other client side errors.";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorText = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorText = "Other network related errors.";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorText = "Network operation timed out.";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorText = "No recognition result matched.";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorText = "RecognitionService busy.";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorText = "Server sends error status.";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorText = "No speech input.";
                break;
        }
        return errorText;
    }
    // endregion

    //endregion

    //region Helpers
    public void setSuggestions(String... suggestions) {
        this.suggestions = suggestions;
        if (getView() != null) {
            updateSuggestions();
        }
    }

    private void updateSuggestions() {
        final Context context = getContext();
        if (context != null) { // Ensure Fragment still attached
            hintView.setVisibility(View.VISIBLE);
            StringBuilder b = new StringBuilder();
            if (suggestions != null && suggestions.length > 0) {
                for (String s : suggestions) {
                    b.append(SEPARATOR).append(s).append("\n");
                }
            }
            suggestionsView.setText(b.toString());
        }
    }

    public void setVoiceResultsListener(VoiceResultsListener voiceResultsListener) {
        this.voiceResultsListener = voiceResultsListener;
    }

    public interface VoiceResultsListener {
        void onVoiceResults(@NonNull ArrayList<String> matches);
    }
    //endregion
}
