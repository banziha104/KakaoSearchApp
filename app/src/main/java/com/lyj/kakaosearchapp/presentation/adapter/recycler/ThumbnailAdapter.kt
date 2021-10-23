package com.lyj.kakaosearchapp.presentation.adapter.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lyj.kakaosearchapp.R


class ThumbnailAdapter : RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail,parent,false)
        return ThumbnailViewHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class ThumbnailViewHolder(view : View) : RecyclerView.ViewHolder(view){

    }
}