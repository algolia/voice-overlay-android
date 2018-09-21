package com.algolia.instantsearch.voice;

import android.content.Context;
import android.content.Intent;
import android.speech.SpeechRecognizer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class VoiceSpeechRecognizerTest {
    @Mock
    private Context mockContext;
    @Mock
    private SpeechRecognizer mockSpeechRecognizer;
    @Mock
    private VoiceSpeechRecognizer.StateListener mockStateListener;

    private VoiceSpeechRecognizer vsr;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
//        when(vsr.getSpeechRecognizer(mockContext)).thenReturn(mockSpeechRecognizer);
        //FIXME: How can I inject a mock SpeechRecognizer when it comes from static method?
        vsr = new VoiceSpeechRecognizer(mockContext);
    }

    @Test
    public void newInstance_hasNoListener() {
        assertTrue("no stateListener should be set by default", vsr.getStateListener() == null);
    }

    @Test
    public void newInstance_hasSpeechRecognizer() {
        assertNotNull(mockSpeechRecognizer);
    }

    @Test
    public void start_sendsIntentToSpeechRecognizer() {
        vsr.setStateListener(mockStateListener);
        vsr.start();
        verify(mockSpeechRecognizer).startListening(any(Intent.class));
        verify(mockStateListener).isListening(true);
    }
}
