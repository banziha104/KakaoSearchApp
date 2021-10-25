package com.lyj.kakaosearchapp.common.extension.android

import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.recyclerview.RecyclerViewScrollEvent
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable

/**
 * RecyclerView 의 EndScroll Event 관찰
 */
fun RecyclerView.observeEndScrollEvents(lifecycleController: RxLifecycleController): Flowable<RecyclerViewScrollEvent> =
    this
        .scrollEvents()
        .disposeByOnDestory(lifecycleController)
        .filter { !it.view.canScrollVertically(RecyclerView.VERTICAL) && this.adapter?.itemCount ?: 0 != 0 } // Item이 비어있는 경우는 제외
        .toFlowable(BackpressureStrategy.LATEST)