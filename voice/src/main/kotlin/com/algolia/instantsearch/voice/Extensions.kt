package com.algolia.instantsearch.voice

import android.os.Bundle
import android.speech.SpeechRecognizer
import java.util.ArrayList


public inline val Bundle.resultsRecognition: ArrayList<String> get() = getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!!
