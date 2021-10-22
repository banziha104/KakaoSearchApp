package com.lyj.kakaosearchapp.data.source.remote.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit

class GithubApiGenerator(
    private val context : Context,
    private val client: OkHttpClient,
    private val callAdapter: CallAdapter.Factory,
    private val converter: Converter.Factory
) : ServiceGenerator {

    companion object {
        private val HEADER_MAP: Map<String, String> = mapOf(
            "Accept" to "KakaoAK"
        )
        private const val BASE_URL = "https://dapi.kakao.com"
    }

    override fun <T> generateService(
        service: Class<T>,
    ): T = TODO()
}

interface ServiceGenerator {
    fun <T> generateService(service: Class<T>): T
}
