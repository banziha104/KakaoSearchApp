package com.lyj.kakaosearchapp.presentation.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.toPublisher
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.domain.model.KakaoSearchListModel
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.domain.usecase.RequestKakaoSearchResultUseCase
import com.lyj.kakaosearchapp.presentation.adapter.recycler.ThumbnailItemEvent

import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Publisher
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val requestKakaoSearchResultUseCase: RequestKakaoSearchResultUseCase
) : ViewModel() {

    var page : Int = KakaoSearchApi.DEFAULT_PAGE

    fun mapToEditTextObserverByPublisher(
        publisher: Publisher<String>,
        lifecycleController: RxLifecycleController
    ): Flowable<String> =
        Flowable
            .fromPublisher(publisher)
            .filter { it.isNotBlank() }
            .disposeByOnDestory(lifecycleController)


    fun mapToStoredDataObserverByPublisher(
        publisher: Publisher<MutableMap<String, KakaoSearchModel>>,
        lifecycleController: RxLifecycleController
    ): Flowable<MutableMap<String, KakaoSearchModel>> =
        Flowable
            .fromPublisher(publisher)
            .disposeByOnDestory(lifecycleController)

    fun mapToKakaoSearchList(
        remoteDataObserver : Flowable<Pair<SearchFragment.SearchFragmentUiEventType,List<KakaoSearchModel>>>,
        storedDataObserver : Flowable<MutableMap<String,KakaoSearchModel>>,
        lifecycleController: RxLifecycleController
    ): Flowable<ThumbnailItemEvent> =
        Flowable
            .combineLatest(
                remoteDataObserver,
                storedDataObserver
            ) { (event,remoteData), storedData ->
                val result = remoteData.map { model ->
                    KakaoSearchListModel(model, storedData[model.siteUrl] != null)
                }
                when(event){
                    is SearchFragment.SearchFragmentUiEventType.EndScroll -> ThumbnailItemEvent.Add(result)
                    else -> ThumbnailItemEvent.Refesh(result)
                }
            }
            .disposeByOnDestory(lifecycleController)

}