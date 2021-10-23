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
import com.jakewharton.rxbinding4.internal.checkMainThread
import com.lyj.kakaosearchapp.R
import com.lyj.kakaosearchapp.action.CustomRecyclerViewAction
import com.lyj.kakaosearchapp.action.CustomTableLayoutAction
import com.lyj.kakaosearchapp.presentation.activity.MainActivity
import com.lyj.kakaosearchapp.action.CustomViewAction
import com.lyj.kakaosearchapp.config.ConstConfig
import com.lyj.kakaosearchapp.matcher.CustomRecyclerViewMatcher
import com.lyj.kakaosearchapp.ui.base.BaseUiTests
import com.lyj.kakaosearchapp.ui.base.MainActivityBaseTests
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTests : MainActivityBaseTests(){


    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * MainActivity 테스트 시나리오
     *
     * 1. 처음 화면에서 SearchFragment 가 Visible 인지 확인
     * 2. SearchFragment 의 RecyclerView 가 비어있는지 확인
     * 3. EditText 에 글자 입력 및 확인
     * 4. SearchButton (검색) 버튼 클릭 확인
     * 5. 데이터를 정상적으로 불러오는 지 확인
     * 6. 첫 번쨰 아이템 클릭 후 저장소에 삽입 및 '저장되었음' 이미지 확인
     * 7. StoreFragment로 넘어가 방금 클릭한 아이템이 들어왔는지 확인
     * 8. 첫 번쨰 아이템 클릭 후 저장소에서 삭제
     * 9. 아이템이 비어있는지 확인
     * 10. SearchFragement로 돌아와 '저장되었음' 이미지가 아닌지 확인
     */
    @Test
    fun `메인_시나리오_테스트`(){
        await(100)

        // 1. 처음 화면에서 SearchFragment 가 Visible 인지 확인
        searchLayoutInteraction.check(matches(isDisplayed()))

        //  2. SearchFragment 의 RecyclerView 가 비어있는지 확인
        searchRecyclerViewInteraction
            .check(matches(CustomRecyclerViewMatcher.withDataEmpty()))

        //  3. EditText 에 글자 입력 및 확인
        editTextInteraction
            .perform(ViewActions.replaceText(ConstConfig.SEARCH_KEYWORD))
            .check(matches(ViewMatchers.withText(ConstConfig.SEARCH_KEYWORD)))

        // 4. SearchButton (검색) 버튼 클릭 확인
        searchButtonInteraciton
            .perform(click())

        await(2000)

        //  5. 데이터를 정상적으로 불러오는 지 확인
        searchRecyclerViewInteraction
            .check(matches(not(CustomRecyclerViewMatcher.withDataEmpty())))

        // 6. 첫 번쨰 아이템 클릭 후 저장소에 삽입 및 '저장되었음' 이미지 확인
        searchRecyclerViewInteraction
            .perform(CustomRecyclerViewAction.clickChildViewWithId(0, R.id.thumbnailItemImgIsStored))
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
        // 7. StoreFragment로 넘어가 방금 클릭한 아이템이 들어왔는지 확인
        tabLayoutInteraction
            .perform(CustomTableLayoutAction.selectTabAtPosition(1))

        storeLayoutInteraction
            .check(matches(not(CustomRecyclerViewMatcher.withDataEmpty())))
//            .perform(CustomRecyclerViewAction.clickChildViewWithId())
        // 8. 첫 번쨰 아이템 클릭 후 저장소에서 삭제
        tabLayoutInteraction
            .perform(CustomRecyclerViewAction.clickChildViewWithId(0, R.id.thumbnailItemImgIsStored))
            .check(matches(CustomRecyclerViewMatcher.withDataEmpty())) // 9. 아이템이 비어있는지 확인

        // 10. SearchFragement로 돌아와 '저장되었음' 이미지가 아닌지 확인
        tabLayoutInteraction
            .perform(CustomTableLayoutAction.selectTabAtPosition(0))

        searchRecyclerViewInteraction
            .perform(CustomRecyclerViewAction.clickChildViewWithId(0, R.id.thumbnailItemImgIsStored))
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
    }
}