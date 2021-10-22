package com.lyj.kakaosearchapp.domain.repository

import com.lyj.kakaosearchapp.domain.model.KakaoImageModel
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.domain.model.KakaoVClipModel
import io.reactivex.rxjava3.core.Single

interface KakaoApiRepository {

    fun requestVClipSeachApi(query :String, sort : Sort, page : Int = 1, size : Int = 10) : Single<List<KakaoVClipModel>>

    fun requestImageSearchApi(query :String, sort : Sort, page : Int = 1, size : Int = 10) : Single<List<KakaoImageModel>>

    enum class Sort{

    }
}


