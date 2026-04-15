package com.example.search.di

import com.example.domain.GetCharactersByNameUseCase
import com.example.domain.impl.GetCharactersByNameUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class SearchModule {
    @Binds
    abstract fun bindGetCharactersByNameUseCase(
        getCharactersByNameUseCaseImpl: GetCharactersByNameUseCaseImpl
    ) : GetCharactersByNameUseCase
}
