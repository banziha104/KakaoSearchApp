package com.lyj.kakaosearchapp.presentation.fragment

import android.util.Log
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
import org.reactivestreams.Publisher
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val requestKakaoSearchResultUseCase: RequestKakaoSearchResultUseCase
) : ViewModel() {

    var page: Int = KakaoSearchApi.DEFAULT_PAGE

    val latestKakaoSearchListModel : MutableList<KakaoSearchListModel> = mutableListOf()

    fun requestKakaoSearchResult(searchKeyword : String, page : Int = this.page): Single<List<KakaoSearchModel>> =
        requestKakaoSearchResultUseCase
            .execute(searchKeyword,page)



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

    enum class KakaoSearchListTrigger {
        REMOTE,
        LOCAL
    }
}