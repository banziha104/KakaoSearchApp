package com.lyj.kakaosearchapp.data.source

import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.extension.testWithAwait
import com.lyj.kakaosearchapp.module.ApiModule
import com.lyj.kakaosearchapp.module.ApiTestModule

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import io.reactivex.rxjava3.core.Single
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
class KakaoSearchApiTests{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var api: KakaoSearchApi

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `이미지_요청_테스트`() {

        Single.zip(
            api
                .requestImageSearch(TestConfig.SEARCH_KEYWORD),
            api
                .requestImageSearch(TestConfig.SEARCH_KEYWORD, page = 2)
        ) { page1, page2 ->
            listOf(page1, page2)
        }
            .testWithAwait()
            .assertNoErrors()
            .assertComplete()
            .assertValue {
                val itemValidation = it.all {
                    val meta = it.meta ?: return@assertValue false
                    val totalCount = meta.totalCount ?: return@assertValue false
                    val document = it.documents ?: return@assertValue false
                    totalCount > 0 && document.isNotEmpty()
                }

                val (page1, page2) = it
                page1.documents?.containsAll(page2.documents ?: return@assertValue false)
                itemValidation &&  !(page1.documents?.containsAll(page2.documents ?: return@assertValue false)!!)
            }

    }

    @Test
    fun `비디오_요청_테스트`() {
        Single.zip(
            api
                .requestVClipSearch(TestConfig.SEARCH_KEYWORD),
            api
                .requestVClipSearch(TestConfig.SEARCH_KEYWORD, page = 2)
        ) { page1, page2 ->
            listOf(page1, page2)
        }
            .testWithAwait()
            .assertNoErrors()
            .assertComplete()
            .assertValue {
                val itemValidation = it.all {
                    val meta = it.meta ?: return@assertValue false
                    val totalCount = meta.totalCount ?: return@assertValue false
                    val document = it.documents ?: return@assertValue false
                    totalCount > 0 && document.isNotEmpty()
                }

                val (page1, page2) = it
                page1.documents?.containsAll(page2.documents ?: return@assertValue false)
                itemValidation &&  !(page1.documents?.containsAll(page2.documents ?: return@assertValue false)!!)
            }
    }
}
