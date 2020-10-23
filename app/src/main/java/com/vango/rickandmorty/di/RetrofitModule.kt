package com.vango.rickandmorty.di

import com.google.gson.GsonBuilder
import com.vango.rickandmorty.model.MainModel
import com.vango.rickandmorty.repository.RetrofitInterface
import com.vango.rickandmorty.util.Converter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(ActivityRetainedComponent::class)
@Module
object RetrofitModule {
    @ActivityRetainedScoped
    @Provides
    fun provideRetrofit() : RetrofitInterface{

        val gson = GsonBuilder() //json converter with custop typeAdapter
            .setLenient()
            .registerTypeAdapter(MainModel::class.java, Converter())
            .create()

        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/")
            //.addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        return retrofit.create(RetrofitInterface::class.java)
    }
}