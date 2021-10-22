package com.lyj.kakaosearchapp.domain.usecase


import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.domain.repository.KakaoApiRepository
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestKakaoSearchResultUseCase @Inject constructor(
    private val repository: KakaoApiRepository
) {
    fun execute() : Single<List<KakaoSearchModel>> = TODO()
}