package com.lyj.kakaosearchapp.domain.usecase

import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.extension.testWithAwait
import com.lyj.kakaosearchapp.module.ApiModule
import com.lyj.kakaosearchapp.module.ApiTestModule
import dagger.hilt.android.testing.*
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
class RequestKakaoSearchResultUseCaseTestsTests : ApiTestModule() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var useCase: RequestKakaoSearchResultUseCase

    @BindValue
    val kakaoApi: KakaoSearchApi = bindKakaoSearchApi()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `실행테스트`() {
        useCase
            .execute(TestConfig.SEARCH_KEYWORD)
            .testWithAwait()
            .assertNoErrors()
            .assertComplete()
            .assertValue { list ->
                list.isNotEmpty() && list.size == 6 &&
                        list.mapNotNull { it.epochMillSeconds }
                            .zipWithNext { a, b -> a <= b }.all { it }
            }
    }
}