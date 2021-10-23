package com.lyj.kakaosearchapp.module

import com.lyj.kakaosearchapp.data.source.remote.entity.KakaoImageResponse
import com.lyj.kakaosearchapp.data.source.remote.entity.KakaoVClipResponse
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.reactivex.rxjava3.core.Single
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import javax.inject.Singleton
import kotlin.random.Random

open class ApiTestModule {
    fun bindKakaoSearchApi() = mock<KakaoSearchApi> {
        on { requestVClipSearch(any(), any(), any(), any()) }.doReturn(
            Single.just(
                KakaoVClipResponse.Response(
                    (0..2).map {
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

        on { requestImageSearch(any(), any(), any(), any()) }.doReturn(
            Single.just(
                KakaoImageResponse.Response(
                    (0..2).map {
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