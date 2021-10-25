package com.lyj.kakaosearchapp.data.repository

import com.lyj.kakaosearchapp.common.util.DateUtils
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.model.KakaoImageModel
import com.lyj.kakaosearchapp.domain.model.KakaoVClipModel
import com.lyj.kakaosearchapp.domain.repository.KakaoApiRepository
import io.reactivex.rxjava3.core.Single

/**
 * KakaoApi 관련 Repository 구현체
 *
 * @param api Kakao API 요청 Service
 * @
 */
class KakaoApiRepositoryImpl(
    private val api: KakaoSearchApi,
) : KakaoApiRepository {

    /**
     * 카카오 검색 API 중 동영상 리스트 요청
     *
     * @param query 검색어
     * @param page Page Offset
     * @param size 페이지당 크기
     * @param sort 정렬 방법 (Default : 시간순)
     */
    override fun requestVClipSeachApi(
        query: String,
        page: Int,
        size: Int,
        sort: KakaoSearchApi.Sort
    ): Single<List<KakaoVClipModel>> =
        api.requestVClipSearch(query, page, size, sort).map { response ->
            response.documents?.filterNotNull()?.map { item ->
                val (epoch, date) = DateUtils.parse(item.datetime)
                item.epochMillSeconds = epoch
                item.date = date
                item
            } ?: listOf()
        }
    /**
     * 카카오 검색 API 중 이미지 리스트 요청
     *
     * @param query 검색어
     * @param page Page Offset
     * @param size 페이지당 크기
     * @param sort 정렬 방법 (Default : 시간순)
     */
    override fun requestImageSearchApi(
        query: String,
        page: Int,
        size: Int,
        sort: KakaoSearchApi.Sort
    ): Single<List<KakaoImageModel>> =
        api.requestImageSearch(query, page, size, sort).map { response ->
            response.documents?.filterNotNull()?.map { item ->
                val (epoch, date) = DateUtils.parse(item.datetime)
                item.epochMillSeconds = epoch
                item.date = date
                item
            } ?: listOf()
        }
}