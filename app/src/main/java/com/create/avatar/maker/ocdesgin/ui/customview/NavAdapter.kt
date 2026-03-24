package com.create.avatar.maker.ocdesgin.ui.customview

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.create.avatar.maker.ocdesgin.base.AbsBaseAdapter
import com.create.avatar.maker.ocdesgin.base.AbsBaseDiffCallBack
import com.create.avatar.maker.ocdesgin.data.model.BodyPartModel
import com.create.avatar.maker.ocdesgin.utils.onClickCustom
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.create.avatar.maker.ocdesgin.R
import com.create.avatar.maker.ocdesgin.databinding.ItemNavigationBinding
import com.create.avatar.maker.ocdesgin.utils.hide
import com.create.avatar.maker.ocdesgin.utils.show

class NavAdapter(context: Context) : AbsBaseAdapter<BodyPartModel, ItemNavigationBinding>(R.layout.item_navigation, DiffNav()) {
    val ct= context
    var posNav = 0
    var onClick: ((Int) -> Unit)? = null

    class DiffNav : AbsBaseDiffCallBack<BodyPartModel>() {
        override fun itemsTheSame(oldItem: BodyPartModel, newItem: BodyPartModel): Boolean {
            return oldItem.icon == newItem.icon
        }

        override fun contentsTheSame(oldItem: BodyPartModel, newItem: BodyPartModel): Boolean {
            return oldItem.icon != newItem.icon
        }

    }

    fun setPos(pos: Int) {
        posNav = pos
    }

    override fun bind(
        binding: ItemNavigationBinding,
        position: Int,
        data: BodyPartModel,
        holder: RecyclerView.ViewHolder
    ) {
        val request = Glide.with(binding.root)
            .load(data.icon)
            .encodeQuality(50)
            .override(180)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

        request.into(binding.imv)
        request.clone().into(binding.imv2)
        binding.apply {
        if (posNav == position) {
            hideNav.hide()
            showNav.show()
        } else {
            hideNav.show()
            showNav.hide()
        }}

        binding.root.onClickCustom {
            onClick?.invoke(position)
        }
    }

}