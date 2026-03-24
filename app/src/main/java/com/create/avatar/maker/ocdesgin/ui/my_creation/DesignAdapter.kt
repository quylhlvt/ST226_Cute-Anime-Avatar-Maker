package com.create.avatar.maker.ocdesgin.ui.my_creation

import androidx.recyclerview.widget.RecyclerView
import com.create.avatar.maker.ocdesgin.base.AbsBaseAdapter
import com.create.avatar.maker.ocdesgin.base.AbsBaseDiffCallBack
import com.create.avatar.maker.ocdesgin.utils.hide
import com.create.avatar.maker.ocdesgin.utils.onSingleClick
import com.create.avatar.maker.ocdesgin.utils.shimmer
import com.create.avatar.maker.ocdesgin.utils.show
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerDrawable
import com.create.avatar.maker.ocdesgin.R
import com.create.avatar.maker.ocdesgin.databinding.ItemMyDesignBinding

class DesignAdapter :
    AbsBaseAdapter<String, ItemMyDesignBinding>(R.layout.item_my_design, DiffCallBack()) {
    var onClick: ((Int, String) -> Unit)? = null
    var arrCheckTick = arrayListOf<Int>()
    var checkLongClick = false
    override fun bind(
        binding: ItemMyDesignBinding,
        position: Int,
        data: String,
        holder: RecyclerView.ViewHolder
    ) {
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }

        Glide.with(binding.root).load(data).placeholder(shimmerDrawable).into(binding.imvImage)
        binding.imvImage.onSingleClick {
            onClick?.invoke(position,"item")
        }
        binding.btnDelete.onSingleClick {
            onClick?.invoke(position,"delete")
        }
        binding.imvImage.setOnLongClickListener  {
            onClick?.invoke(position, "longclick")
            true
        }
        binding.btnSelect.onSingleClick {
            onClick?.invoke(position, "tick")
        }

        if(checkLongClick){
            binding.btnSelect.show()
            if (position in arrCheckTick) {
                binding.btnSelect.setImageResource(R.drawable.imv_check_true)
            } else {
                binding.btnSelect.setImageResource(R.drawable.imv_check_false)
            }
            binding.btnDelete.hide()
        }else{
            binding.btnSelect.hide()
            binding.btnDelete.show()
        }
    }

    class DiffCallBack : AbsBaseDiffCallBack<String>() {
        override fun itemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun contentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem != newItem
        }
    }
}