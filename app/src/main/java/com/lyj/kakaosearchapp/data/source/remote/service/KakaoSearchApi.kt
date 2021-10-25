package com.lyj.kakaosearchapp.data.source.remote.service

import com.google.gson.annotations.SerializedName
import com.lyj.kakaosearchapp.BuildConfig
import com.lyj.kakaosearchapp.data.source.remote.entity.KakaoImageResponse
import com.lyj.kakaosearchapp.data.source.remote.entity.KakaoVClipResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query


/**
 * 카카오 API Service 객체
 */
interface KakaoSearchApi {

    /**
     * 카카오 API Service 상수
     *
     * @property DEFAULT_PAGE 기본 페이지
     * @property DEFAULT_SIZE 페이지당 요청 횟수
     */
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_SIZE = 10
    }
    /**
     *
     * 카카오 검색 API 중 동영상 리스트 요청
     *
     * @param query 검색어
     * @param page Page Offset
     * @param size 페이지당 크기
     * @param sort 정렬 방법 (Default : 시간순)
     */
    @GET("v2/search/vclip")
    fun requestVClipSearch(
        @Query("query") query: String,
        @Query("page") page: Int = DEFAULT_PAGE,
        @Query("size") size: Int = DEFAULT_SIZE,
        @Query("sort") sort: Sort = Sort.RECENCY,
    ): Single<KakaoVClipResponse.Response>

    /**
     * 카카오 검색 API 중 이미지 리스트 요청
     *
     * @param query 검색어
     * @param page Page Offset
     * @param size 페이지당 크기
     * @param sort 정렬 방법 (Default : 시간순)
     */
    @GET("v2/search/image")
    fun requestImageSearch(
        @Query("query") query: String,
        @Query("page") page: Int = DEFAULT_PAGE,
        @Query("size") size: Int = DEFAULT_SIZE,
        @Query("sort") sort: Sort = Sort.RECENCY
    ): Single<KakaoImageResponse.Response>


    /**
     * 정렬 방법
     *
     * @property ACCURACY 정확도순
     * @property RECENCY 최신순
     */
    enum class Sort {
        @SerializedName("accuracy")
        ACCURACY,

        @SerializedName("recency")
        RECENCY
    }
}