package com.vango.rickandmorty.repository

import com.vango.rickandmorty.model.MainModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("/api/character")
    fun getCharacter(@Query("page") pageId: Int): Call<MainModel>
}