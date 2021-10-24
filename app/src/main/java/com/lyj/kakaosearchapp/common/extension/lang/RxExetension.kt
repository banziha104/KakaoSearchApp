package com.lyj.kakaosearchapp.common.extension.lang

import androidx.lifecycle.Lifecycle
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable


fun <T> Observable<T>.disposeByOnStop(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnStop())

fun <T> Observable<T>.disposeByOnPause(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnPause())

fun <T> Observable<T>.disposeByOnDestory(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnDestroy())


fun <T> Flowable<T>.disposeByOnStop(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnStop())

fun <T> Flowable<T>.disposeByOnPause(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnPause())

fun <T> Flowable<T>.disposeByOnDestory(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnDestroy())


fun <T> Single<T>.disposeByOnStop(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnStop())

fun <T> Single<T>.disposeByOnPause(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnPause())

fun <T> Single<T>.disposeByOnDestory(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnDestroy())


fun <T> Maybe<T>.disposeByOnStop(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnStop())

fun <T> Maybe<T>.disposeByOnPause(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnPause())

fun <T> Maybe<T>.disposeByOnDestory(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnDestroy())


fun Completable.disposeByOnStop(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnStop<Any?>())

fun Completable.disposeByOnPause(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnPause<Any?>())

fun Completable.disposeByOnDestory(lifecycleController: RxLifecycleController) =
    this.compose(lifecycleController.disposeByOnDestroy<Any?>())


fun Disposable.disposedBy(
    lifecycleController: RxLifecycleController,
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
) = lifecycleController.add(this, lifecycleEvent)