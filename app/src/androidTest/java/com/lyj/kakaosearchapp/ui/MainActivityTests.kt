package com.lyj.kakaosearchapp.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lyj.kakaosearchapp.R
import com.lyj.kakaosearchapp.action.CustomRecyclerViewAction
import com.lyj.kakaosearchapp.action.CustomTableLayoutAction
import com.lyj.kakaosearchapp.presentation.activity.MainActivity
import com.lyj.kakaosearchapp.config.ConstConfig
import com.lyj.kakaosearchapp.matcher.CustomRecyclerViewMatcher
import com.lyj.kakaosearchapp.presentation.activity.MainTabType
import com.lyj.kakaosearchapp.ui.base.MainActivityBaseTests
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class MainActivityTests : MainActivityBaseTests(){

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun setUp(){
        hiltRule.inject()
    }

    /**
     * MainActivity 테스트 시나리오
     *
     * 1. 처음 화면에서 SearchFragment 가 Visible 인지 확인
     * 2. TabLayout 동작 테스트
     * 3. ViewPager 동작 테스트
     * 4. EditText 에 글자 입력 및 확인
     * 5. SearchButton (검색) 버튼 클릭 확인
     * 6. 데이터를 정상적으로 불러오는 지 확인
     * 7. 첫 번쨰 아이템 클릭 후 저장소에 삽입 및 '저장되었음' 이미지 확인
     * 8. StoreFragment로 넘어가 방금 클릭한 아이템이 들어왔는지 확인
     * 9. 첫 번쨰 아이템 클릭 후 저장소에서 삭제
     * 10. 아이템이 비어있는지 확인
     * 11. SearchFragement로 돌아와 '저장되었음' 이미지가 아닌지 확인
     * 12. 하단으로 이동해 무한 스크롤 확인
     * 13. 상단으로 이동해 Refresh 되는지 확인
     */
    @Test
    fun `메인_시나리오_테스트`(){
        await(100)

        // 1. 처음 화면에서 SearchFragment 가 Visible 인지 확인
        searchLayoutInteraction.check(matches(isDisplayed()))

        // 2. TabLayout 테스트
        // 2-1 TabLayout에서 Store 버튼 클릭 후 StoreFragments 가 Visible 인지 확인
        tabLayoutInteraction.perform(CustomTableLayoutAction.selectTabAtPosition(MainTabType.STORE.ordinal))

        await(500)

        storeLayoutInteraction.check(matches(isDisplayed()))


        // 2-2 TabLayout에서 Search 버튼 클릭 후 SearchFragments 가 Visible 인지 확인
        tabLayoutInteraction.perform(CustomTableLayoutAction.selectTabAtPosition(MainTabType.SEARCH.ordinal))

        await(500)

        searchLayoutInteraction.check(matches(isDisplayed()))

        await(500)

        // 3. ViewPager 동작 테스트
        // 3-1. Left Swipe  후 StoreFragments 가 Visible 인지 확인

        onView(isRoot()).perform(ViewActions.swipeLeft())

        await(100)

        storeLayoutInteraction.check(matches(ViewMatchers.isDisplayed()))


        //  3-2. Right Swipe 후  버튼 클릭 후 SearchFragments 가 Visible 인지 확인
        onView(isRoot()).perform(ViewActions.swipeRight())

        await(100)

        searchLayoutInteraction.check(matches(ViewMatchers.isDisplayed()))


        //  4. EditText 에 글자 입력 및 확인
        editTextInteraction
            .perform(ViewActions.replaceText(ConstConfig.SEARCH_KEYWORD))
            .check(matches(ViewMatchers.withText(ConstConfig.SEARCH_KEYWORD)))

        // 5. SearchButton (검색) 버튼 클릭 확인
        searchButtonInteraciton
            .perform(click())

        await(2000)

        //  6. 데이터를 정상적으로 불러오는 지 확인
        searchRecyclerViewInteraction
            .check(matches(not(CustomRecyclerViewMatcher.withDataEmpty())))

        // 7. 첫 번쨰 아이템 클릭 후 저장소에 삽입 및 '저장되었음' 이미지 확인
        searchRecyclerViewInteraction
            .perform(CustomRecyclerViewAction.clickChildViewWithId(0, R.id.thumbnailItemImgContents))
            .check(
                matches(
                    CustomRecyclerViewMatcher
                        .atPositionWithBitmapDrawable(
                            0,
                            R.id.thumbnailItemImgIsStored,
                            R.drawable.ic_check
                        )
                )
            )


        await(1000)

        // 8. StoreFragment로 넘어가 방금 클릭한 아이템이 들어왔는지 확인
        tabLayoutInteraction
            .perform(CustomTableLayoutAction.selectTabAtPosition(MainTabType.STORE.ordinal))

        await(1000)

        storeLayoutInteraction
            .check(matches(not(CustomRecyclerViewMatcher.withDataEmpty())))

        // 9. 첫 번쨰 아이템 클릭 후 저장소에서 삭제
        storeRecyclerViewInteraction
            .perform(CustomRecyclerViewAction.clickChildViewWithId(0, R.id.thumbnailItemImgContents))
            .check(matches(CustomRecyclerViewMatcher.withDataEmpty())) // 10. 아이템이 비어있는지 확인


        await(1000)

        // 11. SearchFragement로 돌아와 '저장되었음' 이미지가 아닌지 확인
        tabLayoutInteraction
            .perform(CustomTableLayoutAction.selectTabAtPosition(MainTabType.SEARCH.ordinal))

//        * 11. SearchFragement로 돌아와 '저장되었음' 이미지가 아닌지 확인
        searchRecyclerViewInteraction
            .check(
                matches(
                    CustomRecyclerViewMatcher
                        .atPositionWithBitmapDrawable(
                            0,
                            R.id.thumbnailItemImgIsStored,
                            R.drawable.ic_uncheck
                        )
                )
            )



        // 12. 하단으로 이동해 무한 스크롤 확인
        // 12-1 현재 아이템 갯수 확인
        var latestItemCount = 0
        searchRecyclerViewInteraction.check(matches(CustomRecyclerViewMatcher.withDataAssertion { currentItemCount ->
            latestItemCount = currentItemCount
            true
        }))

        searchRecyclerViewInteraction
            .perform(CustomRecyclerViewAction.scrollToEnd())

        await(1000)

        searchRecyclerViewInteraction
            .check(matches(CustomRecyclerViewMatcher.withDataAssertion { adapterItemCount ->
                val isAdapterItemCountBiggerThanLatest = latestItemCount != 0 && adapterItemCount > latestItemCount
                latestItemCount = adapterItemCount
                isAdapterItemCountBiggerThanLatest
            }))


        // 13. 상단으로 이동해 Refresh 되는지 확인
        searchRecyclerViewInteraction
            .perform(CustomRecyclerViewAction.scrollToStart())
            .perform(ViewActions.swipeDown())

        await(1000)

        searchRecyclerViewInteraction
            .check(matches(CustomRecyclerViewMatcher.withDataAssertion { adapterItemCount ->
                adapterItemCount < latestItemCount
            }))
    }
}