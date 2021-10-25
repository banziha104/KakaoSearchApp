package com.lyj.kakaosearchapp.common.extension.android

import androidx.paging.PagingDataAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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
