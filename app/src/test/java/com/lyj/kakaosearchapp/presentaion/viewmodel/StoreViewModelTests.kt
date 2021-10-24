package com.lyj.kakaosearchapp.presentaion.viewmodel


import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.common.util.DateUtils
import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.extension.testWithAwait
import com.lyj.kakaosearchapp.mock.LifecycleMock
import com.lyj.kakaosearchapp.mock.PublisherMock
import com.lyj.kakaosearchapp.mock.PublisherMock.Companion.mockModel
import com.lyj.kakaosearchapp.presentation.fragment.StoreViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.reactivestreams.Publisher
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [TestConfig.SDK_VERSION])
class StoreViewModelTests : PublisherMock{
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    val lifecycleMock by lazy {
        LifecycleMock()
    }

    lateinit var viewModel: StoreViewModel

    lateinit var rxLifecycleController: RxLifecycleController

    @Before
    fun init() {
        hiltRule.inject()
        lifecycleMock.init()
        rxLifecycleController = lifecycleMock.activity
        viewModel = StoreViewModel()
    }

    @After
    fun `테스트_종료`() {
        lifecycleMock.destory()
    }


    @Test
    fun `mapToKakaoSearchListModel_테스트`() {
        viewModel
            .mapToKakaoSearchListModel(
                mockPublisher,
                rxLifecycleController
            )
            .take(1)
            .testWithAwait(10)
            .assertValue {
                it.isNotEmpty() && it[0].siteUrl == mockModel.siteUrl
            }
    }
}
