package com.lyj.kakaosearchapp.presentation.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.toPublisher
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.domain.model.KakaoSearchListModel
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Publisher
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor() : ViewModel() {

    /**
     * MainViewModel의 latestLiveData를 viewModel로 관리하는 객체
     */
    fun mapToKakaoSearchListModel(
        publisher: Publisher<MutableMap<String, KakaoSearchModel>>,
        lifecycleController: RxLifecycleController
    ): Flowable<List<KakaoSearchListModel>> =Flowable
            .fromPublisher(publisher)
            .map { map ->
                map.values.map { KakaoSearchListModel(it, true) }
            }
            .disposeByOnDestory(lifecycleController)
}
