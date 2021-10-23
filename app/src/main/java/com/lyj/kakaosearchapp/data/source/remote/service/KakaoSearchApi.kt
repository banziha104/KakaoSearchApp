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

interface KakaoSearchApi {

    @GET("v2/search/vclip")
    fun requestVClipSearch(
        @Query("query") query: String,
        @Query("sort") sort: Sort = Sort.RECENCY,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Single<KakaoVClipResponse.Response>

    @GET("v2/search/image")
    fun requestImageSearch(
        @Query("query") query: String,
        @Query("sort") sort: Sort = Sort.RECENCY,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Single<KakaoImageResponse.Response>


    enum class Sort {
        @SerializedName("accuracy")
        ACCURACY,

        @SerializedName("recency")
        RECENCY
    }
}