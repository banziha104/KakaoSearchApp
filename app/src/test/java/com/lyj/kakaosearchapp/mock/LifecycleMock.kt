package com.lyj.kakaosearchapp.mock

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.mockito.kotlin.mock
import androidx.appcompat.app.AppCompatActivity
import com.lyj.kakaosearchapp.presentation.activity.MainActivity

import org.robolectric.Robolectric

import org.robolectric.android.controller.ActivityController




class LifecycleMock {

    lateinit var lifecycle : Lifecycle

    lateinit var lifecycleOwner : LifecycleOwner

    lateinit var activity : MainActivity

    private lateinit var controller : ActivityController<*>

    fun init(){
        controller = Robolectric.buildActivity(MainActivity::class.java).create()
        activity = controller.get() as MainActivity
        lifecycleOwner = activity
        lifecycle = activity.lifecycle
        controller.start().resume()
    }

    fun destory(){
        controller.destroy()
    }
}