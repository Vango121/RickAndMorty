package com.vango.rickandmorty.ui


import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vango.rickandmorty.model.Results
import com.vango.rickandmorty.repository.Repository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel @ViewModelInject constructor(val repository: Repository) : ViewModel() {

    private var charactersList: MutableList<Results> = ArrayList()
    private var allCharacters: Deferred<LiveData<List<Results>>> = repository.getAllCharacters()
    private var _favourites = MutableLiveData<List<Results>>()
    val favourites: LiveData<List<Results>>
        get() = _favourites
    private var _characters = MutableLiveData<List<Results>>()
    val characters: LiveData<List<Results>>
        get() = _characters
    private var isFavourites =
        false // check if favourites are active ( activate on buttom "favourite" click)
    private var favouritesList: MutableList<Results> = ArrayList()
    private var _favButtonEnabled = MutableLiveData<Boolean>()
    val favButtonEnabled: LiveData<Boolean>
        get() = _favButtonEnabled
    private var clickedRadioButtonId = 0

    init {
        repository.getCharacters()
    }

    fun getAllCharacters(): LiveData<List<Results>> = runBlocking {
        allCharacters.await()
    }

    fun setList(list: List<Results>) {
        charactersList = list as MutableList<Results>
    }

    private fun getFilteredList(parameter: String): List<Results> =
        charactersList.filter { result -> result.status == parameter }

    fun addFavourite(character: Results) {
        favouritesList.add(character)
        _favourites.postValue(favouritesList)
        //repository.saveFavourites(favouritesList)
        GlobalScope.launch { repository.saveFavourites(favouritesList) }
    }

    fun removeFavourite(character: Results) {
        val id = favouritesList.indexOf(character)
        favouritesList.removeAt(id)
        _favourites.postValue(favouritesList)
        //repository.saveFavourites(favouritesList)
        GlobalScope.launch { repository.saveFavourites(favouritesList) }
    }

    suspend fun getnewFav() {
        val type = object : TypeToken<List<Results>>() {}.type
        repository.getFavourites().collect {
            favouritesList = Gson().fromJson(it, type)
            _favourites.postValue(favouritesList)
        }
    }

    fun onStatusChange(p1: Int) {
        when (p1) {
            0 -> {
                clickedRadioButtonId = 0
                _characters.postValue(charactersList)
            } // all
            1 -> {
                clickedRadioButtonId = 1
                _characters.postValue(getFilteredList("Alive"))
            }
            2 -> {
                clickedRadioButtonId = 2
                _characters.postValue(getFilteredList("Dead"))
            }
            3 -> {
                clickedRadioButtonId = 3
                _characters.postValue(getFilteredList(("unknown")))
            }
        }
    }

    fun favouritesOnClick(view: View) {
        if (!isFavourites) { //was false now clicked so change to true and submit list
            _characters.postValue(favouritesList)
            isFavourites = true
            _favButtonEnabled.postValue(isFavourites)
        } else if (isFavourites) {
            when (clickedRadioButtonId) {
                0 -> _characters.postValue(charactersList)
                1 -> _characters.postValue(getFilteredList("Alive"))
                2 -> _characters.postValue(getFilteredList("Dead"))
                3 -> _characters.postValue(getFilteredList(("unknown")))
            }
            isFavourites = false
            _favButtonEnabled.postValue(isFavourites)
        }
    }
}