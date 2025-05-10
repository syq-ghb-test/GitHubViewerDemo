package com.nikidas.demo.githubviewer.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.nikidas.demo.githubviewer.MainActivity
import com.nikidas.demo.githubviewer.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PopularFragmentTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testPopularTabAndOpenDetail() {
        // 切换到 Popular tab
        onView(withId(R.id.navigation_popular)).perform(click())
        Thread.sleep(10000)
        // 检查列表是否显示
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        // 点击第一个仓库条目
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(0, click())
        )
        // 检查详情页是否显示
        onView(withText("Repository Details")).check(matches(isDisplayed()))
    }
}