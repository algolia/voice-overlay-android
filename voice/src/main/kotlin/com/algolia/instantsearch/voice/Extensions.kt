package com.algolia.instantsearch.voice

import android.os.Bundle
import android.speech.SpeechRecognizer


inline val Bundle.resultsRecognition get() = getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!!
