package com.create.avatar.maker.ocdesgin.data.room

import android.content.Context
import androidx.room.Room
import com.create.avatar.maker.ocdesgin.utils.SingletonHolder


open class BaseRoomDBHelper(context: Context) {
    val db = Room.databaseBuilder(context, AppDB::class.java,"Avatar").build()
    companion object : SingletonHolder<BaseRoomDBHelper, Context>(::BaseRoomDBHelper)
}