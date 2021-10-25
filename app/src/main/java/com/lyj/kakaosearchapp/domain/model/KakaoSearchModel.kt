package com.lyj.kakaosearchapp.domain.model

import java.util.Date

/**
 * Presenation Layer 에서 사용할 Model 객체
 * API 문서상 모두 NotNull 이지만
 * 빈 문자열 등 유효하지 않은 데이터가 넘어오거나
 * 예기치 못한 사항을 핸들링하기 위해 Nullable로 선언
 *
 * @property thumbnail 썸네일 URL
 * @property date 생성일
 * @property epochMillSeconds 생성일의 EpochMillSeconds, 정렬을 위해 사용
 * @property siteUrl 사이트 주소
 */
sealed interface KakaoSearchModel {
    val thumbnail : String?
    val date : Date?
    val epochMillSeconds : Long?
    val siteUrl : String?
}

/**
 * Presenation Layer 에서 사용할 동영상 Model 객체
 */
interface KakaoVClipModel : KakaoSearchModel

/**
 * Presenation Layer 에서 사용할 이미지 Model 객체
 */
interface KakaoImageModel : KakaoSearchModel

/**
 * ThumbnailAdapter 에서 사용하는 Model
 * [KakaoSearchModel]과 동일하며
 * 저장되었는지 여부를 [isStored]로 저장
 *
 * @param isStored 저장된 객체인지 확인
 */
data class KakaoSearchListModel(
    private val kakaoSearchModel: KakaoSearchModel,
    var isStored : Boolean = false
) : KakaoSearchModel by kakaoSearchModel