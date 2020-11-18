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

class MainViewModel @ViewModelInject constructor(val repository: Repository) : ViewModel() {

    private var charactersList: MutableList<Results> = ArrayList()
    private var allCharacters: Deferred<LiveData<List<Results>>> = repository.getAllCharacters()
    private var _favourites = MutableLiveData<List<Results>>()
    val favourites: LiveData<List<Results>>
        get() = _favourites
    private var isFavourites =
        false // check if favourites are active ( activate on buttom "favourite" click)
    var favouritesList: MutableList<Results> = ArrayList()

    init {
        repository.getCharacters()
    }

    fun getAllCharacters(): LiveData<List<Results>> = runBlocking {
        allCharacters.await()
    }

    fun setList(list: List<Results>) {
        charactersList = list as MutableList<Results>
    }

    fun getFilteredList(parameter: String): List<Results> =
        charactersList.filter { result -> result.status.equals(parameter) }

    fun addFavourite(character: Results) {
        favouritesList.add(character)
        _favourites.postValue(favouritesList)
        repository.saveFavourites(favouritesList)
    }

    fun removeFavourite(character: Results) {
        val id = favouritesList.indexOf(character)
        favouritesList.removeAt(id)
        _favourites.postValue(favouritesList)
        repository.saveFavourites(favouritesList)
    }

    fun getFavourites() {
        favouritesList = repository.getFavourites() as MutableList<Results>
        if (favouritesList.size == 0) {
            favouritesList = ArrayList()
        }
        _favourites.postValue(favouritesList)
    }

    fun isFavourite(): Boolean {
        if (!isFavourites) { //was false now clicked so change to true and submit list
            isFavourites = true
        } else if (isFavourites) {
            isFavourites = false
        }
        return isFavourites
    }
}