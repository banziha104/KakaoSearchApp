package com.lyj.kakaosearchapp.domain.usecase


import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.domain.repository.KakaoApiRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * KakaoApi 비디오, 이미지 검색을 결과를 합산
 * 이를 [KakaoSearchModel.epochMillSeconds] 기준으로 정렬하여 반환하는 UseCase
 *
 * @param repository Api 관련 Repository 객체
 */
@Singleton
class RequestKakaoSearchResultUseCase @Inject constructor(
    private val repository: KakaoApiRepository
) {

    /**
     * @param searchKeyword 검색할 키워드
     * @param page 요청할 Page
     * @return 비디오와 이미지 리스트를 합하고 이를 정렬한 리스트 객체
     */
    fun execute(searchKeyword: String, page : Int = KakaoSearchApi.DEFAULT_PAGE): Single<List<KakaoSearchModel>> = Single.zip(
        repository.requestImageSearchApi(searchKeyword,page),
        repository.requestVClipSeachApi(searchKeyword,page)
    ) { images, vClips ->
        images + vClips
    }
        .subscribeOn(Schedulers.io())
        .map { list ->
            list.sortedBy { it.epochMillSeconds }
        }
}