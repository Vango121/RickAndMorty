package com.vango.rickandmorty.ui


import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vango.rickandmorty.model.MainModel
import com.vango.rickandmorty.model.Results
import com.vango.rickandmorty.repository.Repository
import com.vango.rickandmorty.repository.RetrofitCustom
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel @ViewModelInject constructor(repository: Repository) : ViewModel() {
    private var _characters = MutableLiveData<List<Results>>()
    val characters : LiveData<List<Results>>
        get() = _characters
    private var charactersList : MutableList<Results> = ArrayList()
    private var allCharacters: Deferred<LiveData<List<Results>>> = repository.getAllCharacters()
    init {
        repository.getCharacters()
    }
    fun getAllCharacters() : LiveData<List<Results>> = runBlocking {
        allCharacters.await()
    }
    fun setList(list: List<Results>){
        charactersList= list as MutableList<Results>
    }
    fun filterCharacters(parameter : String) : List<Results>{
        val toReturn : MutableList<Results> = ArrayList()
        for (character in charactersList){
            when(character.status){
                parameter -> toReturn.add(character)
            }
        }
        return toReturn
    }
}