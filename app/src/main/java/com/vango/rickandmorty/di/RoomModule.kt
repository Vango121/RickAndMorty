package com.vango.rickandmorty.di

import android.content.Context
import androidx.room.Room
import com.vango.rickandmorty.room.CharacterDao
import com.vango.rickandmorty.room.CharacterDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@InstallIn(ActivityRetainedComponent::class)
@Module
object RoomModule {
    @ActivityRetainedScoped
    @Provides
    fun provideDb(@ApplicationContext context: Context): CharacterDatabase {
        return Room
            .databaseBuilder(
                context,
                CharacterDatabase::class.java,
                "character_db"
            )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @ActivityRetainedScoped
    @Provides
    fun provideCharacterDao(characterDatabase: CharacterDatabase): CharacterDao {
        return characterDatabase.characterDao()
    }
}