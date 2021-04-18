package com.vango.rickandmorty.room

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.*
import com.vango.rickandmorty.model.Results

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(results: Results)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(results: Results)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Results>)

    @Delete
    fun delete(results: Results)

//    @Query("SELECT * FROM characters_table")
//    fun getAllCharacters(): LiveData<List<Results>>

//    @Query("SELECT * FROM characters_table")
//    fun getAllCharacters1(): PagingSource<Int,Results>

    @Query("SELECT * from characters_table LIMIT 0 ,:pageId*30")
    fun getPage(pageId: Int): LiveData<List<Results>>

    @Query("SELECT * from characters_table WHERE status = :query LIMIT 0 ,:pageId*30 ")
    fun getPageFiltered(pageId: Int, query: String): LiveData<List<Results>>
}