package com.vango.rickandmorty


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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel() {
    private var _characters = MutableLiveData<List<Results>>()
    val characters : LiveData<List<Results>>
        get() = _characters

    init {
        Log.i("elo","elo")
    }

    fun print(){
        _characters = repository.getCharacters()
//        val call = retrofitCustom.retrofitInterface.getCharacter(1)
//        call.enqueue(object : Callback<MainModel>{
//            override fun onResponse(call: Call<MainModel>, response: Response<MainModel>) {
//                val retu = response.body()
//                Log.i("elo",retu?.info?.count.toString())
//            }
//
//            override fun onFailure(call: Call<MainModel>, t: Throwable) {
//                print(t.message)
//            }
//
//        })
    }
}