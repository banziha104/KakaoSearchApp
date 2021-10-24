package com.lyj.kakaosearchapp.common.extension.android

import androidx.viewpager2.widget.ViewPager2
import io.reactivex.rxjava3.core.Observable

fun ViewPager2.pageChangeObserver(defaultPosition : Int? = null) : Observable<Int>{
    lateinit var callBack : ViewPager2.OnPageChangeCallback

    return Observable.create<Int> { emitter ->
        callBack = object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                emitter.onNext(position)
            }
        }

        if (defaultPosition != null) emitter.onNext(defaultPosition)

        registerOnPageChangeCallback(callBack)

    }.doOnDispose { unregisterOnPageChangeCallback(callBack) }
}