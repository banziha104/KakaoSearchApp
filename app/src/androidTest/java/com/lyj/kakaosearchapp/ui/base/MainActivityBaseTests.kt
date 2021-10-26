package com.lyj.kakaosearchapp.ui.base

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.lyj.kakaosearchapp.R

open class MainActivityBaseTests : BaseUiTests(){

    internal val tabLayoutInteraction: ViewInteraction by lazy {
        onView(withId(R.id.mainTabLayout))
    }

    internal val viewPagerInteraction : ViewInteraction by lazy {
        onView(withId(R.id.mainViewPager))
    }

    internal val editTextInteraction :  ViewInteraction by lazy {
        onView(withId(R.id.mainInputEditText))
    }

    internal val searchButtonInteraciton : ViewInteraction by lazy{
        onView(withId(R.id.mainBtnSearch))
    }

    internal val searchRecyclerViewInteraction : ViewInteraction by lazy{
        onView(withId(R.id.searchRecyclerView))
    }

    internal val searchLayoutInteraction : ViewInteraction by lazy{
        onView(withId(R.id.searchFrameLayout))
    }

    internal val storeRecyclerViewInteraction : ViewInteraction by lazy{
        onView(withId(R.id.storeRecyclerView))
    }

    internal val storeLayoutInteraction : ViewInteraction by lazy{
        onView(withId(R.id.storeFrameLayout))
    }
}