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
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vango.rickandmorty.model.MainModel
import com.vango.rickandmorty.model.Results
import com.vango.rickandmorty.room.CharacterDao
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
    private var characterList: MutableList<Results> = ArrayList()
    private val characters: MutableLiveData<List<Results>> = MutableLiveData<List<Results>>()
    private val dataStore: DataStore<Preferences> by lazy {
        context.createDataStore(name = "favourites")
    }
    val favList = preferencesKey<String>("favouritesListJson")
    private var pages = 0
    private var charactersCount = 0

    suspend fun getCharacters(pageId: Int) = retrofitCustom.retrofitInterface.getCharacter(pageId)

//    fun getCharacters() {
//        val call = retrofitCustom.retrofitInterface.getCharacter(1)
//        call.enqueue(object : retrofit2.Callback<MainModel> {
//            override fun onResponse(call: Call<MainModel>, response: Response<MainModel>) {
//                response.body()?.results?.let { characterList.addAll(it) }
//                pages = response.body()?.info?.pages!!
//                charactersCount = response.body()?.info?.count!!
//                getNextPage()
//            }
//
//            override fun onFailure(call: Call<MainModel>, t: Throwable) {
//
//            }
//
//        })
//    }
//
//    private fun getNextPage() {
//        for (i in 2..pages) {
//            val call = retrofitCustom.retrofitInterface.getCharacter(i)
//            call.enqueue(object : retrofit2.Callback<MainModel> {
//                override fun onResponse(call: Call<MainModel>, response: Response<MainModel>) {
//                    response.body()?.results?.let { characterList.addAll(it) }
//                    if (characterList.size == charactersCount) {
//                        characters.postValue(characterList)
//                        insertData(characterList) // insert data from web to room db
//                    }
//                }
//
//                override fun onFailure(call: Call<MainModel>, t: Throwable) {
//
//                }
//
//            })
//        }
//    }

    fun insertData(characters: List<Results>) { // insert to room db
        CoroutineScope(Dispatchers.IO).launch {
            for (character in characters) {
                characterDao.insert(character)
            }
        }
    }

    fun getAllCharacters(): Deferred<LiveData<List<Results>>> =
        CoroutineScope(Dispatchers.IO).async {
            characterDao.getAllCharacters()
        }
    fun getchr() : LiveData<PagedList<Results>> = characterDao.getAllCharacters1().toLiveData(15)

//    fun saveFavourites(favourites: List<Results>) {
//        val sharedPreferences =
//            PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
//        sharedPreferences.edit()
//            .putString(
//                "favourites", Gson()
//                    .toJson(favourites)
//            )
//            .apply()
//    }
    suspend fun saveFavourites(favourites: List<Results>) {
        dataStore.edit {
            it[favList] = Gson().toJson(favourites)
        }
    }
//    fun getFavourites(): List<Results> {
//        val sharedPreferences =
//            PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
//        val favourites = sharedPreferences.getString("favourites", "[]")
//        val type = object : TypeToken<List<Results>>() {}.type
//        return Gson().fromJson(favourites, type)
//    }
    fun getFavourites(): Flow<String> = dataStore.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        } }.map { it[favList] ?: "[]" }
}