package com.vango.rickandmorty.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vango.rickandmorty.model.Location
import com.vango.rickandmorty.model.Origin

object RoomConverter {

    //objects to json
    @JvmStatic
    @TypeConverter
    fun convertOrigin(origin: Origin): String {
        return Gson().toJson(origin)
    }

    @JvmStatic
    @TypeConverter
    fun convertLocation(location: Location): String {
        return Gson().toJson(location)
    }

    @JvmStatic
    @TypeConverter
    fun convertEpisodes(episodes: List<String>): String {
        return Gson().toJson(episodes)
    }

    //from json to objects
    @JvmStatic
    @TypeConverter
    fun jsonToLocation(json: String): Location {
        return Gson().fromJson(json, Location::class.java)
    }

    @JvmStatic
    @TypeConverter
    fun jsonToOrigin(json: String): Origin {
        return Gson().fromJson(json, Origin::class.java)
    }

    @JvmStatic
    @TypeConverter
    fun jsonToStringList(json: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, listType)
    }

}