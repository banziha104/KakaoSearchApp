package com.lyj.kakaosearchapp.module

import com.lyj.kakaosearchapp.data.repository.KakaoApiRepositoryImpl
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.repository.KakaoApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideKakaoApiRepository(
        kakaoSearchApi: KakaoSearchApi
    ): KakaoApiRepository = KakaoApiRepositoryImpl(kakaoSearchApi)
}