package com.lyj.kakaosearchapp.common.extension.android

import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController

fun RecyclerView.observeEndScrollEvents(lifecycleController: RxLifecycleController) =
    this
        .scrollEvents()
        .disposeByOnDestory(lifecycleController)
        .filter { !it.view.canScrollVertically(RecyclerView.VERTICAL) && this.adapter?.itemCount ?: 0 != 0 }