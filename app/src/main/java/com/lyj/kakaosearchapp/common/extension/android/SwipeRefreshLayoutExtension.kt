package com.lyj.kakaosearchapp.common.extension.android

import androidx.paging.PagingDataAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

/**
 * Swipe Refresh 를 감지하는 옵저버
 */
fun SwipeRefreshLayout.refreshObserver(): Observable<Unit> =
    Observable.create<Unit> {
        this
            .setOnRefreshListener {
                it.onNext(Unit)
            }
    }
        .doOnDispose { this.setOnRefreshListener(null) }
