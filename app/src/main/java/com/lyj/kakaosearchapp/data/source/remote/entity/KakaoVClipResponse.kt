package com.lyj.kakaosearchapp.data.source.remote.entity

import com.google.gson.annotations.SerializedName

class KakaoVClipResponse {
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

		@field:SerializedName("datetime")
		val datetime: String? = null,

		@field:SerializedName("thumbnail")
		val thumbnail: String? = null,

		@field:SerializedName("author")
		val author: String? = null,

		@field:SerializedName("title")
		val title: String? = null,

		@field:SerializedName("url")
		val url: String? = null,

		@field:SerializedName("play_time")
		val playTime: Int? = null
	)
}