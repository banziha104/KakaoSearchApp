package com.lyj.kakaosearchapp.module

import com.lyj.kakaosearchapp.data.source.remote.ServiceGenerator
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideKakaoSearchApi(serviceGenerator: ServiceGenerator): KakaoSearchApi =
        serviceGenerator.generateService(KakaoSearchApi::class.java)
}