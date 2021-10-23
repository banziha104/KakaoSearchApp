package com.lyj.kakaosearchapp.ui

import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lyj.kakaosearchapp.action.CustomTableLayoutAction
import com.lyj.kakaosearchapp.presentation.activity.MainActivity
import com.lyj.kakaosearchapp.ui.base.MainActivityBaseTests
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ViewPagerTests : MainActivityBaseTests() {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * ViewPager Layout 테스트 시나리오
     *
     * 1. 처음 로딩 후, SearchFragment가 Visible 인지 확인
     * 2. Left Swipe  후 StoreFragments 가 Visible 인지 확인
     * 3. Right Swipe 후  버튼 클릭 후 SearchFragments 가 Visible 인지 확인
     */
    @Test
    fun `ViewPager_테스트`(){
        await(500)

        // 1. 처음 로딩 후, SearchFragment가 Visible 인지 확인
        searchLayoutInteraction.check(matches(ViewMatchers.isDisplayed()))

        // 2. Left Swipe  후 StoreFragments 가 Visible 인지 확인

        viewPagerInteraction.perform(swipeLeft())

        await(100)

        storeLayoutInteraction.check(matches(ViewMatchers.isDisplayed()))


        //  3. Right Swipe 후  버튼 클릭 후 SearchFragments 가 Visible 인지 확인

        viewPagerInteraction.perform(swipeRight())

        await(100)

        searchLayoutInteraction.check(matches(ViewMatchers.isDisplayed()))
    }
}