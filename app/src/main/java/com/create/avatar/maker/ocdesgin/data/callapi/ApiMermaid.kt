package com.create.avatar.maker.ocdesgin.data.callapi

import com.create.avatar.maker.ocdesgin.data.model.CharacterResponse
import retrofit2.http.GET

interface ApiMermaid {
    @GET("api/app/ST221_NunggtsCharacter2")
    suspend fun getAllData(): CharacterResponse
}