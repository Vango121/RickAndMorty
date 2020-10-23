package com.vango.rickandmorty.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.vango.rickandmorty.model.MainModel
import com.vango.rickandmorty.model.Results
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(private val retrofitCustom: RetrofitCustom){
    private  var characterList : MutableList<Results> =ArrayList()
    private val characters: MutableLiveData<List<Results>> = MutableLiveData<List<Results>>()
    private  var pages =0
    private var charactersCount = 0
    fun getCharacters(): MutableLiveData<List<Results>>{
        val call = retrofitCustom.retrofitInterface.getCharacter(1)
        call.enqueue(object : retrofit2.Callback<MainModel> {
            override fun onResponse(call: Call<MainModel>, response: Response<MainModel>) {
                response.body()?.results?.let { characterList.addAll(it) }
                pages = response.body()?.info?.pages!!
                charactersCount = response.body()?.info?.count!!
                getNextPage()
            }

            override fun onFailure(call: Call<MainModel>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
        return characters
    }
    private fun getNextPage(){
        for (i in 2..pages){
        val call = retrofitCustom.retrofitInterface.getCharacter(i)
        call.enqueue(object : retrofit2.Callback<MainModel> {
            override fun onResponse(call: Call<MainModel>, response: Response<MainModel>) {
                response.body()?.results?.let { characterList.addAll(it) }
                Log.i("pages", i.toString())
                Log.i("result", response.body()?.results.toString() + " " + i)
                Log.i("characters", characterList.size.toString())
                if(characterList.size==charactersCount)characters.postValue(characterList)
            }

            override fun onFailure(call: Call<MainModel>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    }
}