package com.create.avatar.maker.ocdesgin.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.create.avatar.maker.ocdesgin.base.AbsBaseAdapter
import com.create.avatar.maker.ocdesgin.base.AbsBaseDiffCallBack
import com.create.avatar.maker.ocdesgin.data.model.SelectedModel
import com.create.avatar.maker.ocdesgin.utils.hide
import com.create.avatar.maker.ocdesgin.utils.onSingleClick
import com.create.avatar.maker.ocdesgin.utils.show
import com.create.avatar.maker.ocdesgin.R
import com.create.avatar.maker.ocdesgin.databinding.ItemColorBgBinding

class ColorAdapter :
    AbsBaseAdapter<SelectedModel, ItemColorBgBinding>(R.layout.item_color_bg, DiffCallBack()) {
    var onClick: ((Int) -> Unit)? = null
    var posSelect = -1
    override fun bind(
        binding: ItemColorBgBinding,
        position: Int,
        data: SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.imvColor.onSingleClick {
            onClick?.invoke(position)
        }
        if(position==0){
            binding.imvColor.setBackgroundResource(R.drawable.imv_add_color)
        }else{
            binding.imvColor.setBackgroundColor(data.color)
        }
        if (data.isSelected) {
            binding.vFocus1.show()
        } else {
            binding.vFocus1.hide()
        }
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