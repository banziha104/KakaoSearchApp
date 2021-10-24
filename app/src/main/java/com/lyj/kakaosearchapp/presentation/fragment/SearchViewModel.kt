package com.lyj.kakaosearchapp.presentation.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.toPublisher
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.domain.model.KakaoSearchListModel
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.domain.usecase.RequestKakaoSearchResultUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Publisher
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val requestKakaoSearchResultUseCase: RequestKakaoSearchResultUseCase
) : ViewModel() {

    fun mapToEditTextObserverByPublisher(
        publisher: Publisher<String>,
        lifecycleController: RxLifecycleController
    ): Flowable<List<KakaoSearchModel>> =
        Flowable
            .fromPublisher(publisher)
            .filter { it.isNotBlank() }
            .flatMapSingle {
                requestKakaoSearchResultUseCase.execute(it)
            }
            .disposeByOnDestory(lifecycleController)


    fun mapToStoredDataObserverByPublisher(
        publisher: Publisher<MutableMap<String, KakaoSearchModel>>,
        lifecycleController: RxLifecycleController
    ): Flowable<MutableMap<String, KakaoSearchModel>> =
        Flowable
            .fromPublisher(publisher)
            .disposeByOnDestory(lifecycleController)

    fun mapToKakaoSearchList(
        remoteDataObserver : Flowable<List<KakaoSearchModel>>,
        storedDataObserver : Flowable<MutableMap<String,KakaoSearchModel>>,
        lifecycleController: RxLifecycleController
    ): Flowable<List<KakaoSearchListModel>> =
        Flowable
            .combineLatest(
                remoteDataObserver,
                storedDataObserver
            ) { remoteData, storedData ->
                remoteData.map { model ->
                    KakaoSearchListModel(model, storedData[model.siteUrl] != null)
                }
            }
            .disposeByOnDestory(lifecycleController)

}