package com.lyj.kakaosearchapp.data.repository

import com.lyj.kakaosearchapp.domain.model.KakaoImageModel
import com.lyj.kakaosearchapp.domain.model.KakaoVClipModel
import com.lyj.kakaosearchapp.domain.repository.KakaoApiRepository
import io.reactivex.rxjava3.core.Single

class KakaoApiRepositoryImpl(

) : KakaoApiRepository {
    override fun requestVClipSeachApi(
        query: String,
        sort: KakaoApiRepository.Sort,
        page: Int,
        size: Int
    ): Single<List<KakaoVClipModel>> {
        TODO("Not yet implemented")
    }

    override fun requestImageSearchApi(
        query: String,
        sort: KakaoApiRepository.Sort,
        page: Int,
        size: Int
    ): Single<List<KakaoImageModel>> {
        TODO("Not yet implemented")
    }
}