package com.lyj.kakaosearchapp.domain.usecase

import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.extension.testWithAwait
import com.lyj.kakaosearchapp.module.ApiTestModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [TestConfig.SDK_VERSION])
class RequestKakaoSearchResultUseCaseTestsTests : ApiTestModule(){

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var useCase: RequestKakaoSearchResultUseCase

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `실행테스트`(){
        useCase
            .execute(TestConfig.SEARCH_KEYWORD)
            .testWithAwait()
            .assertNoErrors()
            .assertComplete()
            .assertValue {
                it.isNotEmpty() && it.size == 6
            }
    }
}