package com.lyj.kakaosearchapp.presentaion.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.toPublisher
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.common.util.DateUtils
import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.domain.usecase.RequestKakaoSearchResultUseCase
import com.lyj.kakaosearchapp.extension.testWithAwait
import com.lyj.kakaosearchapp.mock.LifecycleMock
import com.lyj.kakaosearchapp.mock.PublisherMock
import com.lyj.kakaosearchapp.mock.PublisherMock.Companion.mockModel
import com.lyj.kakaosearchapp.module.ApiModule
import com.lyj.kakaosearchapp.module.ApiTestModule
import com.lyj.kakaosearchapp.presentation.activity.MainViewModel
import com.lyj.kakaosearchapp.presentation.fragment.SearchViewModel
import dagger.hilt.android.testing.*
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.TimeMark

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@UninstallModules(ApiModule::class)
@Config(application = HiltTestApplication::class, sdk = [TestConfig.SDK_VERSION])
class SearchViewModelTests : ApiTestModule(), PublisherMock {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    val kakaoApi: KakaoSearchApi = bindKakaoSearchApi()

    @Inject
    lateinit var requestKakaoSearchResultUseCase: RequestKakaoSearchResultUseCase

    lateinit var viewModel: SearchViewModel

    val lifecycleMock by lazy {
        LifecycleMock()
    }

    @Before
    fun init() {
        hiltRule.inject()
        lifecycleMock.init()
        viewModel = SearchViewModel(requestKakaoSearchResultUseCase)
    }

    @After
    fun `테스트_종료`() {
        lifecycleMock.destory()
        testHandler.onNext(Unit)
    }

    @Test
    fun `mapToEditTextObserverByPublisher_테스트`() {
        viewModel.mapToEditTextObserverByPublisher(
            searchKeywordPublisher,
            lifecycleMock.activity
        )
            .subscribeOn(Schedulers.io())
            .take(1)
            .testWithAwait(10)
            .assertValue {
                it.isNotEmpty() && it.size == 6
            }
    }

    @Test
    fun `mapToStoredDataObserverByPublisher_테스트`() {
        viewModel
            .mapToStoredDataObserverByPublisher(
                mockPublisher,
                lifecycleMock.activity
            )
            .take(1)
            .testWithAwait()
            .assertValue {
                it[mockModel.siteUrl!!] != null
            }
    }

    @Test
    fun `mapToKakaoSearchList_테스트`() {
        viewModel
            .mapToKakaoSearchList(
                Flowable.just(listOf(mockModel)),
                Flowable.just(mutableMapOf(mockModel.siteUrl!! to mockModel)),
                lifecycleMock.activity
            )
            .take(1)
            .testWithAwait(5)
            .assertValue { list ->
                list.any { it.isStored }
            }
    }
}
