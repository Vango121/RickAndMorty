package com.vango.rickandmorty.util

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.vango.rickandmorty.model.MainModel
import java.lang.reflect.Type

class Converter : JsonDeserializer<MainModel> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MainModel {
        val type = object : TypeToken<MainModel>(){}.type
        val list : MainModel = Gson().fromJson(json,type)
        return list
    }
}