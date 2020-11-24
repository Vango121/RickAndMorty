package com.vango.rickandmorty.room

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.vango.rickandmorty.model.Results

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(results: Results)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(results: Results)

    @Delete
    fun delete(results: Results)

    @Query("SELECT * FROM characters_table")
    fun getAllCharacters(): LiveData<List<Results>>

    @Query("SELECT * FROM characters_table")
    fun getAllCharacters1(): DataSource.Factory<Int, Results>
}