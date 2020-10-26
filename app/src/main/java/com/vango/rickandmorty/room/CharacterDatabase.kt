package com.vango.rickandmorty.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vango.rickandmorty.model.Results

@Database(entities = arrayOf(Results::class),version = 1,exportSchema = true)
@TypeConverters(RoomConverter::class)
abstract class CharacterDatabase : RoomDatabase(){
    abstract fun characterDao(): CharacterDao
}