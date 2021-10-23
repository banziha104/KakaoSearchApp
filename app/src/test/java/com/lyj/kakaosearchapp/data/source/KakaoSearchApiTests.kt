package com.lyj.kakaosearchapp.data.source

import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.extension.testWithAwait

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [TestConfig.SDK_VERSION])
class KakaoSearchApiTests {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var api : KakaoSearchApi

    @Before
    fun init(){
        hiltRule.inject()
    }

    @Test
    fun `이미지_요청_테스트`(){
        api
            .requestImageSearch(TestConfig.SEARCH_KEYWORD)
            .testWithAwait()
            .assertNoErrors()
            .assertComplete()
            .assertValue {
                val meta = it.meta ?: return@assertValue false
                val totalCount = meta.totalCount ?: return@assertValue  false
                val document = it.documents ?: return@assertValue false
                totalCount > 0 && document.isNotEmpty()
            }

    }
    @Test
    fun `비디오_요청_테스트`() {
        api
            .requestVClipSearch(TestConfig.SEARCH_KEYWORD)
            .testWithAwait()
            .assertNoErrors()
            .assertComplete()
            .assertValue {
                val meta = it.meta ?: return@assertValue false
                val totalCount = meta.totalCount ?: return@assertValue false
                val document = it.documents ?: return@assertValue false
                totalCount > 0 && document.isNotEmpty()
            }
    }
}
