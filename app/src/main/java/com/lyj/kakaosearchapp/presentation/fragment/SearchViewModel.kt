package com.lyj.kakaosearchapp.presentation.fragment

import androidx.lifecycle.ViewModel
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.model.KakaoSearchListModel
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.domain.usecase.RequestKakaoSearchResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.reactivestreams.Publisher
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val requestKakaoSearchResultUseCase: RequestKakaoSearchResultUseCase
) : ViewModel() {

    /**
     * 현재 페이지
     */
    var page: Int = KakaoSearchApi.DEFAULT_PAGE

    /**
     * 마지막에 계산된 KakaoSearchListModel 리스트 객체
     */
    val latestKakaoSearchListModel : MutableList<KakaoSearchListModel> = mutableListOf()


    /**
     * Kakao Api 요청
     */
    fun requestKakaoSearchResult(searchKeyword : String, page : Int = this.page): Single<List<KakaoSearchModel>> =
        requestKakaoSearchResultUseCase
            .execute(searchKeyword,page)
            .subscribeOn(Schedulers.io())


    /**
     * MainActivity의 SearchKeyword LiveData를 RxStream으로 변환
     */
    fun mapToEditTextObserverByPublisher(
        publisher: Publisher<String>,
        lifecycleController: RxLifecycleController
    ): Flowable<String> =
        Flowable
            .fromPublisher(publisher)
            .filter { it.isNotBlank() }
            .disposeByOnDestory(lifecycleController)


    /**
     * MainActivity의 latestStoredData LiveData를 RxStream으로 변환
     */
    fun mapToStoredDataObserverByPublisher(
        publisher: Publisher<MutableMap<String, KakaoSearchModel>>,
        lifecycleController: RxLifecycleController
    ): Flowable<MutableMap<String, KakaoSearchModel>> =
        Flowable
            .fromPublisher(publisher)
            .disposeByOnDestory(lifecycleController)

    /**
     * 불러온 원격 데이터와 저장 데이터 변경을 결합하여
     * ThumbnailAdapter에 전달할 데이터 스트림을 만드는 함수
     *
     *
     * @param remoteDataObserver 발생시킨 UI 이벤트와, 원격으로 요청한 List를 Pair로 전달하는 데이터 스트림
     * @param storedDataObserver 저장된 데이터 변경을 감지하는 데이터 스트림
     * @param lifecycleController lifecycle에 맞게 onComplete를 호출하여 메모리 관리
     * @param onRemoteResultEmpty 원격에서 불러온 데이터가 없는 경우의 콜백
     * @return  UI 이벤트와 저장 데이터 변경을 감지하는 데이터 스트림
     */
    inline fun mapToKakaoSearchList(
        remoteDataObserver: Flowable<Pair<SearchFragment.SearchFragmentUiEventType, List<KakaoSearchModel>>>,
        storedDataObserver: Flowable<MutableMap<String, KakaoSearchModel>>,
        lifecycleController: RxLifecycleController,
        crossinline onRemoteResultEmpty : () -> Unit,
    ): Flowable<List<KakaoSearchListModel>> {
        var trigger: KakaoSearchListTrigger = KakaoSearchListTrigger.REMOTE
        return Flowable
            .combineLatest(
                remoteDataObserver.doOnNext { trigger = KakaoSearchListTrigger.REMOTE },
                storedDataObserver.doOnNext { trigger = KakaoSearchListTrigger.LOCAL }
            ) { (event, remoteData), storedData ->
                when(trigger){
                    KakaoSearchListTrigger.REMOTE ->{
                        latestKakaoSearchListModel.apply {
                            if (remoteData.isEmpty()){
                                onRemoteResultEmpty()
                                clear()
                                return@apply
                            }
                            if (event !is SearchFragment.SearchFragmentUiEventType.EndScroll){
                                latestKakaoSearchListModel.clear()
                            }
                            latestKakaoSearchListModel.addAll(remoteData.map { model ->
                                KakaoSearchListModel(model, storedData[model.siteUrl] != null)
                            })
                        }
                    }
                    KakaoSearchListTrigger.LOCAL ->{
                        latestKakaoSearchListModel.map {
                            it.copy(isStored = storedData[it.siteUrl] != null )
                        }
                    }
                }
            }
            .disposeByOnDestory(lifecycleController)
    }

    /**
     * combineLatest에서 이벤트 발생자를 구분
     */
    enum class KakaoSearchListTrigger {
        REMOTE,
        LOCAL
    }
}