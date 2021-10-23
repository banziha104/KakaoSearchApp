package com.lyj.kakaosearchapp.data.repository

import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.repository.KakaoApiRepository
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@UninstallModules(ApiModule::class)
@Config(application = HiltTestApplication::class, sdk = [TestConfig.SDK_VERSION])
class KakaoApiRepositoryTests : ApiTestModule(){

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    val kakaoApi : KakaoSearchApi = bindKakaoSearchApi()

    @Inject
    lateinit var repository: KakaoApiRepository


    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `이미지_요청_테스트`() {
        repository
            .requestImageSearchApi(TestConfig.SEARCH_KEYWORD)
            .testWithAwait()
            .assertNoErrors()
            .assertComplete()
            .assertValue { list ->
                list.count() == 3
            }

    }

    @Test
    fun `비디오_요청_테스트`() {
        repository
            .requestVClipSeachApi(TestConfig.SEARCH_KEYWORD)
            .testWithAwait()
            .assertNoErrors()
            .assertComplete()
            .assertValue { list ->
                list.count() == 3
            }
    }
}