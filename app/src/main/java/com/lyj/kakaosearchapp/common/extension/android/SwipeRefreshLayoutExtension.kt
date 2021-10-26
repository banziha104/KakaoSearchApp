package com.lyj.kakaosearchapp.common.extension.android

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.rxjava3.core.Observable

/**
 * SwipeRefreshLayout 의 Refresh Event를 관찰
 */
fun SwipeRefreshLayout.refreshObserver(): Observable<Unit> =
    Observable.create<Unit> {
        this
            .setOnRefreshListener {
                it.onNext(Unit)
            }
    }
        .doOnDispose { this.setOnRefreshListener(null) }
