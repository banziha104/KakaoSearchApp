package com.lyj.kakaosearchapp.module

import com.lyj.kakaosearchapp.data.source.remote.entity.KakaoImageResponse
import com.lyj.kakaosearchapp.data.source.remote.entity.KakaoVClipResponse
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.reactivex.rxjava3.core.Single
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import javax.inject.Singleton
import kotlin.random.Random

@HiltAndroidTest
@UninstallModules(ApiModule::class)
open class ApiTestModule {
    @BindValue
    @Singleton
    val kakaoSearchApi: KakaoSearchApi = mock<KakaoSearchApi> {
        on { requestVClipSearch("") }.doReturn(
            Single.just(
                KakaoVClipResponse.Response(
                    (0..3).map {
                        KakaoVClipResponse.DocumentsItem(
                            "2021-09-${(0..30).random()}T20:30:05.000+09:00",
                            thumbnail = "thumnail${it}"
                        )
                    },
                    KakaoVClipResponse.Meta(
                        totalCount = 3
                    )
                )
            )
        )

        on { requestImageSearch("") }.doReturn(
            Single.just(
                KakaoImageResponse.Response(
                    (0..3).map {
                        KakaoImageResponse.DocumentsItem(
                            datetime = "2021-09-${(0..30).random()}T20:30:05.000+09:00",
                            thumbnailUrl = "thumnailUrl${it}"
                        )
                    },
                    KakaoImageResponse.Meta(
                        totalCount = 3
                    )
                )
            )
        )
    }
}