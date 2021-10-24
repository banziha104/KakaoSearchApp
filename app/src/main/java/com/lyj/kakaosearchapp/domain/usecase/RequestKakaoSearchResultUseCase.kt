package com.lyj.kakaosearchapp.domain.usecase


import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.domain.repository.KakaoApiRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestKakaoSearchResultUseCase @Inject constructor(
    private val repository: KakaoApiRepository
) {
    fun execute(searchKeyword: String, page : Int = KakaoSearchApi.DEFAULT_PAGE): Single<List<KakaoSearchModel>> = Single.zip(
        repository.requestImageSearchApi(searchKeyword,page),
        repository.requestVClipSeachApi(searchKeyword,page)
    ) { images, vClips ->
        images + vClips
    }
        .subscribeOn(Schedulers.io())
        .map { list ->
            list.sortedBy { it.epochTimes }
        }

}