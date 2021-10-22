package com.lyj.kakaosearchapp.data.source.remote.service

import com.lyj.kakaosearchapp.data.source.remote.entity.KakaoImageResponse
import com.lyj.kakaosearchapp.data.source.remote.entity.KakaoVClipResponse
import io.reactivex.rxjava3.core.Single

interface KakaoSearchApi {
    fun requestVClipSearch(
        query: String,
        sort: Sort,
        page: Int = 1,
        size: Int = 10
    ): Single<KakaoVClipResponse.Response>

    fun requestImageSearch(
        query: String,
        sort: Sort,
        page: Int = 1,
        size: Int = 10
    ): Single<KakaoImageResponse.Response>


    enum class Sort {

    }
}