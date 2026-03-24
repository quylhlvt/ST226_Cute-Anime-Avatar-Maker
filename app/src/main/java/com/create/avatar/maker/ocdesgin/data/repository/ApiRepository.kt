package com.create.avatar.maker.ocdesgin.data.repository

import android.util.Log
import com.create.avatar.maker.ocdesgin.data.callapi.ApiHelper
import com.create.avatar.maker.ocdesgin.data.model.CharacterResponse
import com.create.avatar.maker.ocdesgin.utils.CONST
import com.create.avatar.maker.ocdesgin.utils.DataHelper
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiHelper: ApiHelper) {
    suspend fun getFigure(): CharacterResponse? {
        try {
            CONST.BASE_URL = CONST.BASE_URL_1
            return apiHelper.apiMermaid1.getAllData()
//            return null
        } catch (e: Exception) {
            Log.d(DataHelper.TAG, "getFigure: $e")
            try {
                CONST.BASE_URL = CONST.BASE_URL_2
//                return null
                return apiHelper.apiMermaid2.getAllData()
            } catch (e: Exception) {
                Log.d(DataHelper.TAG, "getFigure: $e")
                return null
            }
        }
    }
}