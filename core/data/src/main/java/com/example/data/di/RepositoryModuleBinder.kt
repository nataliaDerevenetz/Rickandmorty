package com.example.data.di

import com.example.data.repository.ExplorerRepositoryImpl
import com.example.domain.repository.ExplorerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleBinder {

    @Singleton
    @Binds
    abstract fun bindDataSourceRepository(
        dataSourceRepositoryImpl: ExplorerRepositoryImpl
    ) : ExplorerRepository

}