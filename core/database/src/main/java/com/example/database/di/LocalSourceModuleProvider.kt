package com.example.database.di

import android.content.Context
import androidx.room.Room
import com.example.database.db.ExplorerRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalSourceModuleProvider {

    @Provides
    fun provideCharacterFilterDao(database: ExplorerRoomDatabase) = database.characterFilterDao()

    @Provides
    fun provideCharacterRemoteKeyDao(database: ExplorerRoomDatabase) = database.characterRemoteKeyDao()

    @Provides
    fun provideEpisodeDao(database: ExplorerRoomDatabase) = database.episodeDao()

    @Provides
    fun provideEpisodeRemoteKeyDao(database: ExplorerRoomDatabase) = database.episodeRemoteKeyDao()

    @Provides
    fun provideEpisodeInfoDao(database: ExplorerRoomDatabase) = database.episodeInfoDao()

    @Provides
    fun provideCharacterDao(database: ExplorerRoomDatabase) = database.characterDao()

    @Provides
    @Singleton
    fun providesLocalDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        ExplorerRoomDatabase::class.java,
        "explorer-database"
    ).build()
}