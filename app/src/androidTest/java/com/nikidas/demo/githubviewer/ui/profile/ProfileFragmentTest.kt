package com.nikidas.demo.githubviewer.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.nikidas.demo.githubviewer.MainActivity
import com.nikidas.demo.githubviewer.R
import org.hamcrest.Matchers.anyOf
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    fun isViewDisplayed(viewId: Int): Boolean {
        return try {
            onView(withId(viewId)).check(matches(isDisplayed()))
            true
        } catch (e: Exception) {
            false
        }
    }

    @Test
    fun testProfileTabAndCheckUserInfo() {
        onView(withId(R.id.navigation_profile)).perform(click())
        Thread.sleep(1500)
        Assert.assertTrue(isViewDisplayed(R.id.btnLogin))

    }
}