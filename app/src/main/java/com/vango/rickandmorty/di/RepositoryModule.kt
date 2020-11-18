package com.vango.rickandmorty.di

import android.content.Context
import com.vango.rickandmorty.repository.Repository
import com.vango.rickandmorty.repository.RetrofitCustom
import com.vango.rickandmorty.room.CharacterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@InstallIn(ActivityRetainedComponent::class)
@Module
object RepositoryModule {
    @ActivityRetainedScoped
    @Provides
    fun provideRepository(
        retrofitCustom: RetrofitCustom,
        characterDao: CharacterDao,
        @ApplicationContext context: Context
    ): Repository {
        return Repository(retrofitCustom, characterDao, context)
    }
}