package com.lyj.kakaosearchapp.ui

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
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
class TabLayoutTests : MainActivityBaseTests() {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Tab Layout 테스트 시나리오
     *
     * 1. 처음 로딩 후, SearchFragment가 Visible 인지 확인
     * 2. TabLayout에서 Store 버튼 클릭 후 StoreFragments 가 Visible 인지 확인
     * 3. TabLayout에서 Search 버튼 클릭 후 SearchFragments 가 Visible 인지 확인
     */
    @Test
    fun `TabLayout_테스트`() {
        await(500)

        // 1. 처음 로딩 후, SearchFragment가 Visible 인지 확인
        searchLayoutInteraction.check(matches(isDisplayed()))

        // 2. TabLayout에서 Store 버튼 클릭 후 StoreFragments 가 Visible 인지 확인
        tabLayoutInteraction.perform(CustomTableLayoutAction.selectTabAtPosition(1))

        await(100)

        storeLayoutInteraction.check(matches(isDisplayed()))


        // 3. TabLayout에서 Search 버튼 클릭 후 SearchFragments 가 Visible 인지 확인
        tabLayoutInteraction.perform(CustomTableLayoutAction.selectTabAtPosition(0))

        await(100)

        searchLayoutInteraction.check(matches(isDisplayed()))
    }
}