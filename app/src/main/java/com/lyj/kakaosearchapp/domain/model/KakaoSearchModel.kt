package com.lyj.kakaosearchapp.domain.model

import java.util.Date

interface KakaoSearchModel {
    val thumbnail : String?
    val date : Date?
    val epochTimes : Long?
}

interface KakaoVClipModel : KakaoSearchModel

interface KakaoImageModel : KakaoSearchModel