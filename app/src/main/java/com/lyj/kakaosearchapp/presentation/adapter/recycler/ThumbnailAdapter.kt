package com.lyj.kakaosearchapp.presentation.adapter.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import com.lyj.kakaosearchapp.R
import com.lyj.kakaosearchapp.common.extension.lang.isNotNullAndTrue
import com.lyj.kakaosearchapp.databinding.ItemThumbnailBinding
import com.lyj.kakaosearchapp.domain.model.KakaoSearchListModel
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * SearchFragment와 StoreFragment에 RecyclerView의 Adapter
 *
 * @param dataObserver 데이터 변경을 감지하고 이를 [KakaoModelDiffUtils]을 통해 갱신
 * @param onClicked 아이템이 눌렸을 때의 callBack
 */
class ThumbnailAdapter(
    private val dataObserver: Flowable<List<KakaoSearchListModel>>,
    private val onClicked: (KakaoSearchModel) -> Unit
) : RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>() {

    /**
     * 마지막으로 반영된 아이템
     */
    private var latestData: List<KakaoSearchListModel> = listOf()

    init {
        dataObserver
            .observeOn(Schedulers.computation())
            .map { newItems ->
                val diffUtils = KakaoModelDiffUtils(latestData, newItems)
                DiffUtil.calculateDiff(diffUtils,false) to newItems
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (diffResult, newItems) ->
                /**
                 * [List.toList]를 통해 DeepCopy 하여
                 * 원본을 복사한 리스트 생성
                 * toList 제거시 [KakaoModelDiffUtils]에서 동일한 객체를 비교하게 되어
                 * 데이터가 바뀌어도 갱신이 안됨
                 */
                latestData = newItems.toList()
                diffResult.dispatchUpdatesTo(this)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val binding =
            ItemThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThumbnailViewHolder(binding)
    }

    override fun getItemCount(): Int = latestData.size

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        holder.bind(latestData[position])
    }

    inner class ThumbnailViewHolder(private val binding: ItemThumbnailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding
                .thumbnailItemImgContents
                .clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe({
                    onClicked(latestData[bindingAdapterPosition])
                }, {
                    it.printStackTrace()
                })
        }

        fun bind(item: KakaoSearchListModel) {
            Glide
                .with(itemView)
                .let { requestManager ->

                    val checkDrawble = if (item.isStored.isNotNullAndTrue()) R.drawable.ic_check else R.drawable.ic_uncheck

                    if (binding.thumbnailItemImgContents.tag != item.thumbnail) {
                        requestManager
                            .load(item.thumbnail)
                            .into(binding.thumbnailItemImgContents.apply {
                                tag = item.thumbnail
                            })
                    }

                    requestManager
                        .load(checkDrawble)
                        .into(binding.thumbnailItemImgIsStored.apply {
                            tag = checkDrawble
                        })
                }

        }
    }
}

/**
 * 이전 데이터와 새로운 데이터간에 차이를 찾고 이를 Adapter에 반영하는 객체
 *
 * @param oldItems 기존 아이템
 * @param newItems 새로운 아이템
 * @see DiffUtil.Callback
 */
class KakaoModelDiffUtils(
    private val oldItems: List<KakaoSearchListModel>,
    private val newItems: List<KakaoSearchListModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].siteUrl == newItems[newItemPosition].siteUrl

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}