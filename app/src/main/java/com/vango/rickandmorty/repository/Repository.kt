package com.vango.rickandmorty.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.google.gson.Gson
import com.vango.rickandmorty.model.Results
import com.vango.rickandmorty.room.CharacterDao
import com.vango.rickandmorty.room.CharacterDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class Repository @Inject constructor(
    private val retrofitCustom: RetrofitCustom,
    private val characterDao: CharacterDao, @ApplicationContext val context: Context
) {
    private val dataStore: DataStore<Preferences> by lazy {
        context.createDataStore(name = "favourites")
    }
    val favList = preferencesKey<String>("favouritesListJson")


    suspend fun getCharacters(pageId: Int) = retrofitCustom.retrofitInterface.getCharacter(pageId)

    fun insertData(characters: List<Results>) { // insert to room db
        CoroutineScope(Dispatchers.IO).launch {
            for (character in characters) {
                characterDao.insert(character)
            }
        }
    }

    fun getCharactersRoom(pageId: Int, filterId: Int): LiveData<List<Results>> {
        when(filterId){
            1-> return characterDao.getPageFiltered(pageId, "Alive")
            2-> return characterDao.getPageFiltered(pageId, "Dead")
            3-> return characterDao.getPageFiltered(pageId, "unknown")
            else -> return characterDao.getPage(pageId)
        }
    }


    fun getAllCharacters(): Deferred<LiveData<List<Results>>> =
        CoroutineScope(Dispatchers.IO).async {
            characterDao.getAllCharacters()
        }


    suspend fun saveFavourites(favourites: List<Results>) {
        dataStore.edit {
            it[favList] = Gson().toJson(favourites)
        }
    }

    fun getFavourites(): Flow<String> = dataStore.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { it[favList] ?: "[]" }
}