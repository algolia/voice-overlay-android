package com.algolia.instantsearch.voice.demo;


import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityWithPermissionTest {

    @Rule
    public IntentsTestRule intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO);

//    @Before
//    public void setup() {
//        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
//    }

    @Test
    public void clickPermissionButton_displaysPermissionOverlay() {
        // When clicking on the buttonPermission
        onView(withId(R.id.buttonPermission))
                .perform(click());

        // the permission overlay should display
        onView(withId(R.id.voicePermission))
                .check(matches(isDisplayed()));
        // the input overlay should not
        onView(withId(R.id.voiceInput))
                .check(doesNotExist());
    }

    @Test
    public void clickInputButton_displaysInputOverlay() {
        // When clicking on the buttonVoice
        onView(withId(R.id.buttonVoice))
                .perform(click());

        // the input overlay should display
        onView(withId(R.id.voiceInput))
                .check(matches(isDisplayed()));
        // the permission overlay should not
        onView(withId(R.id.voicePermission))
                .check(doesNotExist());
    }

    @Test
    public void clickInput_sendsSpeakIntentAndDisplaysResults() {
        onView(withId(R.id.results))
                .check(matches(isDisplayed()));

        // (then) return recognition results
        intending(hasAction(equalTo(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)))
                .respondWith(new ActivityResult(Activity.RESULT_OK, new Intent().putExtra(
                        SpeechRecognizer.RESULTS_RECOGNITION, new String[]{"I am uttering."})));

        // when clicking on the buttonVoice, thus starting the speechRecognizer
        onView(withId(R.id.buttonVoice))
                .perform(click());

        // then after closing the overlay
        onView(withId(R.id.close))
                .perform(click());

        // expect the recognition results to be displayed
        onView(withId(R.id.results))
                .check(matches(withText("I am uttering.")));
    }
}
