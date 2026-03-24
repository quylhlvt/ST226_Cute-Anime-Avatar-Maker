package com.create.avatar.maker.ocdesgin.custom.listener.listenerdraw

import android.view.MotionEvent
import com.create.avatar.maker.ocdesgin.custom.DrawView


interface DrawEvent {
    fun onActionDown(tattooView: DrawView?, event: MotionEvent?)
    fun onActionMove(tattooView: DrawView?, event: MotionEvent?)
    fun onActionUp(tattooView: DrawView?, event: MotionEvent?)
}