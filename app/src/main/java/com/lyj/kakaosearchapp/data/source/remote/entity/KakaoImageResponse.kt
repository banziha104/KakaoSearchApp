package com.lyj.kakaosearchapp.data.source.remote.entity

import com.google.gson.annotations.SerializedName
import com.lyj.kakaosearchapp.domain.model.KakaoImageModel
import com.lyj.kakaosearchapp.domain.model.KakaoVClipModel
import java.util.*

class KakaoImageResponse {
	data class Response(
		@field:SerializedName("documents")
		val documents: List<DocumentsItem?>? = null,

		@field:SerializedName("meta")
		val meta: Meta? = null
	)

	data class Meta(

		@field:SerializedName("total_count")
		val totalCount: Int? = null,

		@field:SerializedName("is_end")
		val isEnd: Boolean? = null,

		@field:SerializedName("pageable_count")
		val pageableCount: Int? = null
	)

	data class DocumentsItem(

		@field:SerializedName("doc_url")
		val docUrl: String? = null,

		@field:SerializedName("datetime")
		val datetime: String? = null,

		@field:SerializedName("display_sitename")
		val displaySitename: String? = null,

		@field:SerializedName("image_url")
		val imageUrl: String? = null,

		@field:SerializedName("width")
		val width: Int? = null,

		@field:SerializedName("collection")
		val collection: String? = null,

		@field:SerializedName("thumbnail_url")
		val thumbnailUrl: String? = null,

		@field:SerializedName("height")
		val height: Int? = null
	) : KakaoImageModel{
		override val thumbnail: String?
			get() = thumbnailUrl
		override var date: Date? = null
		override var epochTimes: Long? = null
	}
}