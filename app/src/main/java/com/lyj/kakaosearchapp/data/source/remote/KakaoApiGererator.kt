package com.lyj.kakaosearchapp.data.source.remote

import android.content.Context
import com.lyj.kakaosearchapp.BuildConfig
import com.lyj.kakaosearchapp.data.source.remote.interceptor.KakaoHeaderInterceptor
import com.lyj.kakaosearchapp.data.source.remote.interceptor.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class KakaoApiGenerator(
    private val context : Context,
    private val callAdapter: CallAdapter.Factory,
    private val converter: Converter.Factory,
    private val callBack : NetworkConnectionInterceptor.OnCheckNetworkConnection
) : ServiceGenerator {

    companion object {
        private val KAKAO_API_HEADER_MAP: Map<String, String> by lazy {
            mapOf(
                "Authorization" to "KakaoAK ${BuildConfig.KAKAO_API_KEY}"
            )
        }
        private const val BASE_URL = "https://dapi.kakao.com"
    }

    override fun <T> generateService(
        service: Class<T>,
    ): T = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder().let {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.HEADERS
            it
                .addInterceptor(logger)
                .addInterceptor(NetworkConnectionInterceptor(context,callBack))
                .addInterceptor(KakaoHeaderInterceptor(KAKAO_API_HEADER_MAP))
                .connectTimeout(20, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
        })
        .addCallAdapterFactory(callAdapter)
        .addConverterFactory(converter)
        .build()
        .create(service)
}

interface ServiceGenerator {
    fun <T> generateService(service: Class<T>): T
}
