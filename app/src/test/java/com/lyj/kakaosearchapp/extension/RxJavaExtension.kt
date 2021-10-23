package com.lyj.kakaosearchapp.extension

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


fun <T> Observable<T>.testWithAwait(seconds: Long = 3L) = this
    .subscribeOn(Schedulers.io())
    .test()
    .awaitDone(seconds, TimeUnit.SECONDS)

fun <T> Maybe<T>.testWithAwait(seconds: Long = 3L) = this
    .subscribeOn(Schedulers.io())
    .test()
    .awaitDone(seconds, TimeUnit.SECONDS)


fun <T> Single<T>.testWithAwait(seconds: Long = 3L) = this
    .subscribeOn(Schedulers.io())
    .test()
    .awaitDone(seconds, TimeUnit.SECONDS)


fun <T> Flowable<T>.testWithAwait(seconds: Long = 3L) = this
    .subscribeOn(Schedulers.io())
    .test()
    .awaitDone(seconds, TimeUnit.SECONDS)

fun Completable.testWithAwait(seconds: Long = 3L) = this
    .subscribeOn(Schedulers.io())
    .test()
    .awaitDone(seconds, TimeUnit.SECONDS)
