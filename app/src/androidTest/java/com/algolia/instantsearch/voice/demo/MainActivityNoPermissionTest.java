package com.algolia.instantsearch.voice.demo;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityNoPermissionTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(MainActivity.class);

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
    public void clickInputButton_displaysPermissionOverlay() {
        // When clicking on the buttonVoice
        onView(withId(R.id.buttonVoice))
                .perform(click());

        // the permission overlay should display
        onView(withId(R.id.voicePermission))
                .check(matches(isDisplayed()));
        // the input overlay should not
        onView(withId(R.id.voiceInput))
                .check(doesNotExist());
    }
}
