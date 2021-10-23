package com.lyj.kakaosearchapp.matcher

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Description
import org.hamcrest.Matcher
import java.lang.Exception


// RecyclerView 관련 Custom Matcher를 모아둔 객체
object CustomRecyclerViewMatcher {

    val appContext: Context by lazy{
        InstrumentationRegistry.getInstrumentation().targetContext
    }

    fun withDataEmpty(): Matcher<View> =
        object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("recyclerview item count test")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val adapter = view.adapter ?: throw AdapterNullPointerException()
                val itemCount = adapter.itemCount
                return itemCount == 0
            }
        }

    fun withDataAssertion(assertion: (Int) -> Boolean): Matcher<View> =
        object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("recyclerview item count test")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val adapter = view.adapter ?: throw AdapterNullPointerException()
                val itemCount = adapter.itemCount
                return assertion(itemCount)
            }
        }
    fun atPositionWithBitmapDrawable(
        position: Int,
        @IdRes viewId: Int,
        @DrawableRes drawableRes: Int
    ): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position ")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                val imageView : ImageView = viewHolder.itemView.findViewById<ImageView>(viewId)
                return imageView.tag == drawableRes
            }
        }
    }

    fun atPosition(position: Int, assertion: (View) -> Boolean): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                return assertion(viewHolder.itemView)
            }
        }
    }
}

class AdapterNullPointerException : Exception("adapter is null")

class DrawableNotFoundedException(@DrawableRes id : Int) : Exception("$id is not founded")