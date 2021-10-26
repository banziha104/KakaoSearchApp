package com.lyj.kakaosearchapp.presentaion.viewmodel

import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.model.KakaoSearchListModel
import com.lyj.kakaosearchapp.domain.usecase.RequestKakaoSearchResultUseCase
import com.lyj.kakaosearchapp.extension.testWithAwait
import com.lyj.kakaosearchapp.mock.LifecycleMock
import com.lyj.kakaosearchapp.mock.PublisherMock
import com.lyj.kakaosearchapp.mock.PublisherMock.Companion.mockModel
import com.lyj.kakaosearchapp.module.ApiModule
import com.lyj.kakaosearchapp.module.ApiTestModule
import com.lyj.kakaosearchapp.presentation.fragment.SearchFragment
import com.lyj.kakaosearchapp.presentation.fragment.SearchViewModel
import dagger.hilt.android.testing.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

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
                it.isNotBlank()
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
        viewModel.latestKakaoSearchListModel.add(KakaoSearchListModel(mockModel,false))
        viewModel
            .mapToKakaoSearchList(
                Flowable.just(SearchFragment.SearchFragmentUiEventType.Search(TestConfig.SEARCH_KEYWORD) to listOf(mockModel)),
                Flowable.just(mutableMapOf(mockModel.siteUrl!! to mockModel)),
                lifecycleMock.activity
            ){}
            .take(1)
            .testWithAwait(10)
            .assertValue { list ->
                list.any { it.isStored }
            }
    }
}
