package com.lyj.kakaosearchapp.domain.model

import java.util.Date

sealed interface KakaoSearchModel {
    val thumbnail : String?
    val date : Date?
    val epochTimes : Long?
    val siteUrl : String?
}

interface KakaoVClipModel : KakaoSearchModel

interface KakaoImageModel : KakaoSearchModel

data class KakaoSearchListModel(
    private val kakaoSearchModel: KakaoSearchModel,
    var isStored : Boolean = false
) : KakaoSearchModel by kakaoSearchModel