package com.vango.rickandmorty.ui


import android.view.View
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vango.rickandmorty.model.Results
import com.vango.rickandmorty.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    private var charactersList: MutableList<Results> = ArrayList()


    private var allCharacters: LiveData<List<Results>> = repository.getCharactersRoom(1, 0)
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
    private val loadTrigger = MutableLiveData<Int>()
    val paginationLiveData: LiveData<List<Results>> = // switch map when more data is needed
        Transformations.switchMap(loadTrigger) {
            getCharacters(loadTrigger.value!!)
        } // get data from room

    suspend fun getDataFromWeb() { // and insert it into room db
        val charactersList: MutableList<Results> = ArrayList()
        val mainModel = repository.getCharacters(1)
        charactersList.addAll(mainModel.results)
        val pageCount = mainModel.info.pages
        for (i in 2..pageCount) {
            charactersList.addAll(repository.getCharacters(i).results)
        }
        repository.insertData(charactersList)
    }

    fun changePage(pageId: Int) {
        loadTrigger.value = pageId
    }

    private fun getCharacters(pageId: Int) = runBlocking { // get characters from room db
        repository.getCharactersRoom(pageId, clickedRadioButtonId)
    }


    fun getAllCharacters(): LiveData<List<Results>> =
        allCharacters


    fun setList(list: List<Results>) {
        charactersList = list as MutableList<Results>
    }

    private fun getFilteredList(parameter: String): List<Results> = // filter characters list (all, alive, dead, unknown)
        charactersList.filter { result -> result.status == parameter }

    fun favClicked(character: Results) { // handle fav button click
        if (!favouritesList.contains(character)) { // if fav list don't contain character add
            addFavourite(character)
        } else { // if fav list contain character remove it from list
            removeFavourite(character)
        }
    }

    private fun addFavourite(character: Results) { //add fav logic
        favouritesList.add(character)
        _favourites.postValue(favouritesList)
        viewModelScope.launch { repository.saveFavourites(favouritesList) }
    }

    fun removeFavourite(character: Results) { //remove ccharacter from favourite logic
        val id = favouritesList.indexOf(character)
        favouritesList.removeAt(id)
        _favourites.postValue(favouritesList)
        viewModelScope.launch { repository.saveFavourites(favouritesList) }
    }

    suspend fun getnewFav() { // get favourites from datastore
        val type = object : TypeToken<List<Results>>() {}.type
        repository.getFavourites().collect {
            favouritesList = Gson().fromJson(it, type)
            _favourites.postValue(favouritesList)
        }
    }

    fun onStatusChange(p1: Int) { // handle filtering option change with databinding
        when (p1) {
            0 -> {
                clickedRadioButtonId = 0
                _characters.postValue(charactersList)
            } // all
            1 -> { // alive
                clickedRadioButtonId = 1
                _characters.postValue(getFilteredList("Alive"))
            }
            2 -> { // dead
                clickedRadioButtonId = 2
                _characters.postValue(getFilteredList("Dead"))
            }
            3 -> { // unknown
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
            when (clickedRadioButtonId) { // return to the same state as it was before entering favourites
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