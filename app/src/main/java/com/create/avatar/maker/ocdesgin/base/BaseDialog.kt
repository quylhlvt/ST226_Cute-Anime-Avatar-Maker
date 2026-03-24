package com.create.avatar.maker.ocdesgin.base

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.create.avatar.maker.ocdesgin.utils.SystemUtils
import com.create.avatar.maker.ocdesgin.utils.showSystemUI
import com.create.avatar.maker.ocdesgin.R

abstract class BaseDialog <DB : ViewDataBinding>(var context : Activity, var canAble: Boolean) :
    Dialog(context, R.style.BaseDialog) {

    lateinit var binding: DB

    abstract fun getContentView(): Int
    abstract fun initView()
    abstract fun bindView()
    override fun onStart() {
        super.onStart()
        window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtils.setLocale(context)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), getContentView(), null, false)
        setContentView(binding.root)
        setCancelable(canAble)
        initView()
        bindView()
        setOnDismissListener {
            context.showSystemUI()
        }
    }
}