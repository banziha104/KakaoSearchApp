package com.lyj.kakaosearchapp.module

import android.content.Context
import com.lyj.kakaosearchapp.R
import com.lyj.kakaosearchapp.common.extension.android.longToast
import com.lyj.kakaosearchapp.data.source.remote.KakaoApiGenerator
import com.lyj.kakaosearchapp.data.source.remote.ServiceGenerator
import com.lyj.kakaosearchapp.data.source.remote.interceptor.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideCallAdapterFactory(): CallAdapter.Factory = RxJava3CallAdapterFactory.create()

    @Provides
    @Singleton
    fun providerConvertFactory(): Converter.Factory = GsonConverterFactory
        .create()

    @Provides
    @Singleton
    fun provideServiceGenerator(
        @ApplicationContext context: Context,
        callAdapter: CallAdapter.Factory,
        converter: Converter.Factory
    ): ServiceGenerator = KakaoApiGenerator(context, callAdapter, converter,
        NetworkConnectionInterceptor.OnCheckNetworkConnection { isNetworkConnetable ->
            if (!isNetworkConnetable)
                MainScope().launch(Dispatchers.Main) {
                    context.longToast(R.string.network_not_reachable)
                }
        })
}