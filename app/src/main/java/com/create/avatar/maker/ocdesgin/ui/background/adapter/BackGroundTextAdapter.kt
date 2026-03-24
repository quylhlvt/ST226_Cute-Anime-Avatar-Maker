package com.create.avatar.maker.ocdesgin.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.create.avatar.maker.ocdesgin.base.AbsBaseAdapter
import com.create.avatar.maker.ocdesgin.base.AbsBaseDiffCallBack
import com.create.avatar.maker.ocdesgin.data.model.SelectedModel
import com.create.avatar.maker.ocdesgin.utils.onSingleClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.create.avatar.maker.ocdesgin.R
import com.create.avatar.maker.ocdesgin.databinding.ItemTextBgBinding

class BackGroundTextAdapter :
    AbsBaseAdapter<SelectedModel, ItemTextBgBinding>(R.layout.item_text_bg, DiffCallBack()) {
    var onClick: ((Int) -> Unit)? = null
    override fun bind(
        binding: ItemTextBgBinding,
        position: Int,
        data: SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.imv.onSingleClick {
            onClick?.invoke(position)
        }
        Glide.with(binding.root).load(data.path).encodeQuality(70)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(binding.imv)
    }

    class DiffCallBack : AbsBaseDiffCallBack<SelectedModel>() {
        override fun itemsTheSame(
            oldItem: SelectedModel,
            newItem: SelectedModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun contentsTheSame(
            oldItem: SelectedModel,
            newItem: SelectedModel
        ): Boolean {
            return oldItem.path != newItem.path || oldItem.isSelected != newItem.isSelected
        }

    }
}