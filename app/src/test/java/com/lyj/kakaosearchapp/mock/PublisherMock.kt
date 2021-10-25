package com.lyj.kakaosearchapp.mock

import com.lyj.kakaosearchapp.common.util.DateUtils
import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

interface PublisherMock {
    companion object {
        private val _testHandler: PublishSubject<Unit> = PublishSubject.create()

        val mockModel = mock<KakaoSearchModel> {
            val (mockEpoch, mockDate) = DateUtils.parse("2021-09-30T20:30:05.000+09:00")
            on { thumbnail } doReturn "thumbnail"
            on { date } doReturn mockDate
            on { epochMillSeconds } doReturn mockEpoch
            on { siteUrl } doReturn "siteUrl"
        }
    }

    val testHandler: PublishSubject<Unit>
        get() = _testHandler

    val searchKeywordPublisher: Publisher<String>
        get() =
            Observable
                .interval(0, 200L, TimeUnit.MILLISECONDS)
                .startWithItem(1)
                .map { TestConfig.SEARCH_KEYWORD }
                .takeUntil(testHandler)
                .toFlowable(BackpressureStrategy.LATEST)

    val mockPublisher: Publisher<MutableMap<String, KakaoSearchModel>>
        get() = Observable
            .interval(0, 200L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .startWithItem(1)
            .map {
                mutableMapOf<String, KakaoSearchModel>(mockModel.siteUrl!! to mockModel)
            }
            .takeUntil(testHandler)
            .toFlowable(BackpressureStrategy.LATEST)


    fun testFinish() = testHandler.onNext(Unit)

}