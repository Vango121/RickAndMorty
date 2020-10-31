package com.vango.rickandmorty.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vango.rickandmorty.model.MainModel
import com.vango.rickandmorty.model.Results
import com.vango.rickandmorty.room.CharacterDao
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(private val retrofitCustom: RetrofitCustom,
                                     private val characterDao: CharacterDao){
    private  var characterList : MutableList<Results> =ArrayList()
    private val characters: MutableLiveData<List<Results>> = MutableLiveData<List<Results>>()
    private  var pages =0
    private var charactersCount = 0
    fun getCharacters(){
        val call = retrofitCustom.retrofitInterface.getCharacter(1)
        call.enqueue(object : retrofit2.Callback<MainModel> {
            override fun onResponse(call: Call<MainModel>, response: Response<MainModel>) {
                response.body()?.results?.let { characterList.addAll(it) }
                pages = response.body()?.info?.pages!!
                charactersCount = response.body()?.info?.count!!
                getNextPage()
            }

            override fun onFailure(call: Call<MainModel>, t: Throwable) {

            }

        })
    }
    private fun getNextPage(){
        for (i in 2..pages){
        val call = retrofitCustom.retrofitInterface.getCharacter(i)
        call.enqueue(object : retrofit2.Callback<MainModel> {
            override fun onResponse(call: Call<MainModel>, response: Response<MainModel>) {
                response.body()?.results?.let { characterList.addAll(it) }
                if(characterList.size==charactersCount){
                    characters.postValue(characterList)
                    insertData(characterList) // insert data from web to room db
                }
            }

            override fun onFailure(call: Call<MainModel>, t: Throwable) {

            }

        })
    }
    }
    fun insertData(characters : List<Results>){ // insert to room db
        CoroutineScope(Dispatchers.IO).launch {
            for (character in characters){
                characterDao.insert(character)
            }
        }
    }

    fun getAllCharacters() : Deferred<LiveData<List<Results>>> =
        CoroutineScope(Dispatchers.IO).async {
            characterDao.getAllCharacters()
        }
}