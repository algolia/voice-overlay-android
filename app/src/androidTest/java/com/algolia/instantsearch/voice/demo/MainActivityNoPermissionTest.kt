package com.algolia.instantsearch.voice.demo


import android.Manifest
import android.content.pm.PackageManager
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.content.ContextCompat

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import org.junit.Assert.fail

/**
 * Tests the behavior of the application when no permission are set.
 * **Make sure that no permissions have been granted on the test device!**
 * If any, reset them with `adb shell pm reset-permissions com.algolia.instantsearch.voice.demo`
 * before running these tests.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityNoPermissionTest {


    @Rule
    @JvmField
    var mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun clickPermissionButton_displaysPermissionOverlay() {
        // When clicking on the buttonPermission
        onView(withId(R.id.buttonPermission))
            .perform(click())

        // the permission overlay should display
        onView(withId(R.id.voicePermission))
            .check(matches(isDisplayed()))
        // the input overlay should not
        onView(withId(R.id.voiceInput))
            .check(doesNotExist())
    }

    /* Disabled until Virtual Device Testing stops granting permissions before testing,
    see https://discuss.bitrise.io/t/why-does-virtual-device-testing-always-grant-permissions-before-testing/6341

    @Test
    fun clickInputButton_displaysPermissionOverlay() {
        if (ContextCompat.checkSelfPermission(mActivityRule.getActivity(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            fail("The permission was already granted, cannot test MainActivityNoPermission!")
        }
        // When clicking on the buttonVoice
        onView(withId(R.id.buttonVoice))
            .perform(click())

        // the permission overlay should display
        onView(withId(R.id.voicePermission))
            .check(matches(isDisplayed()))
        // the input overlay should not
        onView(withId(R.id.voiceInput))
            .check(doesNotExist())
    }
    */
}
