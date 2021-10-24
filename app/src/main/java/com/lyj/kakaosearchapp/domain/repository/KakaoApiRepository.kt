package com.lyj.kakaosearchapp.domain.repository

import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.model.KakaoImageModel
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.domain.model.KakaoVClipModel
import io.reactivex.rxjava3.core.Single

interface KakaoApiRepository {

    fun requestVClipSeachApi(
        query: String,
        page: Int = KakaoSearchApi.DEFAULT_PAGE,
        size: Int = KakaoSearchApi.DEFAULT_SIZE,
        sort: KakaoSearchApi.Sort = KakaoSearchApi.Sort.ACCURACY
    ): Single<List<KakaoVClipModel>>

    fun requestImageSearchApi(
        query: String,
        page: Int = KakaoSearchApi.DEFAULT_PAGE,
        size: Int = KakaoSearchApi.DEFAULT_SIZE,
        sort: KakaoSearchApi.Sort = KakaoSearchApi.Sort.ACCURACY
    ): Single<List<KakaoImageModel>>

}


