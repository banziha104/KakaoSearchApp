package com.lyj.kakaosearchapp.common.rx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.reactivestreams.Publisher


/**
 * Disposable 관리를 위해 LifecycleOwner 과 연동을 추상화한 인터페이스
 */
interface RxLifecycleController : LifecycleOwner {
    val rxLifecycleObserver: RxLifecycleObserver

    fun add(disposable: Disposable, lifecycleEvent: Lifecycle.Event) =
        rxLifecycleObserver.add(disposable, lifecycleEvent)

    fun <T> disposeByOnStop(): LifecycleTransformer<T> =
        rxLifecycleObserver.transformer(disposeBy = Lifecycle.Event.ON_STOP)

    fun <T> disposeByOnPause(): LifecycleTransformer<T> =
        rxLifecycleObserver.transformer(disposeBy = Lifecycle.Event.ON_PAUSE)

    fun <T> disposeByOnDestroy(): LifecycleTransformer<T> =
        rxLifecycleObserver.transformer(disposeBy = Lifecycle.Event.ON_DESTROY)
}

/**
 * [Lifecycle.Event] 를 감지하고, 해당 이벤트에 맞게 Disposable을 관리하는 객체
 *
 * @param lifecycleOwner 관찰할 LifecycleOwner
 */
class RxLifecycleObserver(
    private val lifecycleOwner: LifecycleOwner
) : LifecycleObserver {

    private val onPauseDisposable = CompositeDisposable()
    private val onStopDisposable = CompositeDisposable()
    private val onDestroyDisposable = CompositeDisposable()

    private val publisher: BehaviorSubject<Lifecycle.Event> = BehaviorSubject.create()

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun add(disposable: Disposable, lifecycleEvent: Lifecycle.Event) {
        when (lifecycleEvent) {
            Lifecycle.Event.ON_PAUSE -> onPauseDisposable.add(disposable)
            Lifecycle.Event.ON_STOP -> onStopDisposable.add(disposable)
            Lifecycle.Event.ON_DESTROY -> onDestroyDisposable.add(disposable)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun any(owner: LifecycleOwner, event: Lifecycle.Event) {
        publisher.onNext(event)
        if (event == Lifecycle.Event.ON_DESTROY) {
            publisher.onComplete()
            lifecycleOwner.lifecycle.removeObserver(this)
        }

        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                onPauseDisposable.clear()
            }
            Lifecycle.Event.ON_STOP -> {
                onStopDisposable.clear()
            }
            Lifecycle.Event.ON_DESTROY -> {
                onDestroyDisposable.dispose()
                onPauseDisposable.dispose()
                onStopDisposable.dispose()
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        }
    }

    fun <T> transformer(disposeBy: Lifecycle.Event) =
        LifecycleTransformer<T>(disposeBy, publisher)
}


/**
 * RxJava의 compose()의 파라미터로 사용되는 객체
 * [Lifecycle.Event] 를 전달 받고, 해당 이벤트가 감지되면 onComplete() 를 호출하도록 하는 객체
 *
 * @param lifecycleEvent onComplete()를 호출할 이벤트
 * @param lifecycleObserver Lifecycle 을 관찰하는 객체 [RxLifecycleObserver.publisher]
 */
class LifecycleTransformer<T>(
    private val lifecycleEvent: Lifecycle.Event,
    lifecycleObserver: Observable<Lifecycle.Event>
) : ObservableTransformer<T, T>,
    FlowableTransformer<T, T>,
    SingleTransformer<T, T>,
    MaybeTransformer<T, T>,
    CompletableTransformer {

    private val observable = lifecycleObserver.filter { it == lifecycleEvent }

    override fun apply(upstream: Observable<T>): ObservableSource<T> =
        upstream.takeUntil(observable)

    override fun apply(upstream: Flowable<T>): Publisher<T> =
        upstream.takeUntil(observable.toFlowable(BackpressureStrategy.LATEST))

    override fun apply(upstream: Single<T>): SingleSource<T> =
        upstream.takeUntil(observable.firstOrError())

    override fun apply(upstream: Maybe<T>): MaybeSource<T> =
        upstream.takeUntil(observable.firstElement())

    override fun apply(upstream: Completable): CompletableSource =
        upstream.ambWith(observable.concatMapCompletable { Completable.complete() })
}