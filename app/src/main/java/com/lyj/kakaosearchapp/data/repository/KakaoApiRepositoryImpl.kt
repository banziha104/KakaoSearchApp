package com.lyj.kakaosearchapp.data.repository

import com.lyj.kakaosearchapp.common.util.DateUtils
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.model.KakaoImageModel
import com.lyj.kakaosearchapp.domain.model.KakaoVClipModel
import com.lyj.kakaosearchapp.domain.repository.KakaoApiRepository
import io.reactivex.rxjava3.core.Single

class KakaoApiRepositoryImpl(
    private val api: KakaoSearchApi,
) : KakaoApiRepository {
    override fun requestVClipSeachApi(
        query: String,
        sort: KakaoSearchApi.Sort,
        page: Int,
        size: Int
    ): Single<List<KakaoVClipModel>> =
        api.requestVClipSearch(query, sort, page, size).map { response ->
            response.documents?.filterNotNull()?.map { item ->
                val (epoch, date) = DateUtils.parse(item.datetime)
                item.epochTimes = epoch
                item.date = date
                item
            } ?: listOf()
        }

    override fun requestImageSearchApi(
        query: String,
        sort: KakaoSearchApi.Sort,
        page: Int,
        size: Int
    ): Single<List<KakaoImageModel>> =
        api.requestImageSearch(query, sort, page, size).map { response ->
            response.documents?.filterNotNull()?.map { item ->
                val (epoch, date) = DateUtils.parse(item.datetime)
                item.epochTimes = epoch
                item.date = date
                item
            } ?: listOf()
        }
}