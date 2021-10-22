package com.lyj.kakaosearchapp.domain.model

import java.time.LocalDateTime

interface KakaoSearchModel {
    val thumbnail : String
    val dataTime : LocalDateTime
}

interface KakaoVClipModel : KakaoSearchModel

interface KakaoImageModel : KakaoSearchModel