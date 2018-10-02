package com.algolia.instantsearch.voice.demo;

import android.Manifest;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.AssertionFailedError;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityWithPermissionTest {

    @Rule
    public ActivityTestRule testRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO);

    @Test
    public void clickPermissionButton_displaysPermissionOverlay() {
        when_clickButtonPermission();

        // the permission overlay should display
        onView(withId(R.id.voicePermission))
                .check(matches(isDisplayed()));
        // the input overlay should not
        onView(withId(R.id.voiceInput))
                .check(doesNotExist());
    }

    @Test
    public void clickInputButton_displaysInputOverlay() {
        when_clickButtonVoice();

        check_displaysInputOverlay();
        check_displaysListeningOrError();
    }

    @Test
    public void clickInput_thenCancel_displaysError() {
        when_clickButtonVoice();

        // Then clicking on the mic button
        onView(withId(R.id.microphone))
                .perform(click());

        check_displaysError();
    }

    //region Helpers
    private void when_clickButtonPermission() {
        // When clicking on the buttonPermission
        onView(withId(R.id.buttonPermission))
                .perform(click());
    }

    private void when_clickButtonVoice() {
        // When clicking on the buttonVoice
        onView(withId(R.id.buttonVoice))
                .perform(click());
    }

    private void check_displaysInputOverlay() {
        // the input overlay should display
        onView(withId(R.id.voiceInput))
                .check(matches(isDisplayed()));
        // the permission overlay should not
        onView(withId(R.id.voicePermission))
                .check(doesNotExist());
    }

    private void check_displaysListening() {
        // listening should be displayed
        onView(withId(R.id.title))
                .check(matches(withText(R.string.input_title_listening)));
        onView(withId(R.id.subtitle))
                .check(matches(withText(R.string.input_subtitle_listening)));
        // No hint should be visible
        onView(withId(R.id.hint))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    private void check_displaysError() {
        // the error should be displayed
        onView(withId(R.id.title))
                .check(matches(withText(R.string.input_title_error)));
        onView(withId(R.id.subtitle))
                .check(matches(withText(R.string.input_subtitle_error)));

        // And the button to be labeled try again
        onView(withId(R.id.hint))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.input_hint_error)));
    }

    private void check_displaysListeningOrError() {
        try {
            check_displaysListening();
        } catch (AssertionFailedError e) {
            // Maybe we are on emulator -> no SpeechRecognizer?
            // in this case, we should see error displayed
            check_displaysError();
        }
    }
    //endregion
}
