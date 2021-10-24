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


class ThumbnailAdapter(
    private val dataObserver: Flowable<List<KakaoSearchListModel>>,
    private val onClicked: (KakaoSearchModel) -> Unit
) : RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>() {

    private var latestData: List<KakaoSearchListModel> = listOf()

    init {
        dataObserver
            .observeOn(Schedulers.computation())
            .map { newItems ->
                val diffUtils = KakaoModelDiffUtils(latestData, newItems)
                DiffUtil.calculateDiff(diffUtils) to newItems
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (diffResult, newItems) ->
                diffResult.dispatchUpdatesTo(this)
                latestData = newItems
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

                    if (binding.thumbnailItemImgContents.tag != item.thumbnail) {
                        requestManager
                            .load(item.thumbnail)
                            .into(binding.thumbnailItemImgContents.apply {
                                tag = item.thumbnail
                            })
                    }
                    requestManager
                        .load(if (item.isStored.isNotNullAndTrue()) R.drawable.ic_check else R.drawable.ic_uncheck)
                        .into(binding.thumbnailItemImgIsStored)
                }

        }
    }
}

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