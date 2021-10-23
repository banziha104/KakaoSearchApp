package com.lyj.kakaosearchapp.ui.base

import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import com.lyj.kakaosearchapp.action.CustomViewAction

open class BaseUiTests {

    internal fun await(long : Long){
        Espresso.onView(ViewMatchers.isRoot()).perform(CustomViewAction.waitFor(long))
    }
}