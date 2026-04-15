package com.example.episodes.di

import com.example.domain.GetCharacterDetailsUseCase
import com.example.domain.GetEpisodeDetailsUseCase
import com.example.domain.GetEpisodesUseCase
import com.example.domain.impl.GetCharacterDetailsUseCaseImpl
import com.example.domain.impl.GetEpisodeDetailsUseCaseImpl
import com.example.domain.impl.GetEpisodesUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class EpisodeModule {
    @Binds
    abstract fun bindGetEpisodesUseCase(
        getEpisodesUseCaseImpl: GetEpisodesUseCaseImpl
    ) : GetEpisodesUseCase

    @Binds
    abstract fun bindGetEpisodeDetailsUseCase(
        getEpisodeDetailsUseCaseImpl: GetEpisodeDetailsUseCaseImpl
    ) : GetEpisodeDetailsUseCase

    @Binds
    abstract fun bindGetCharacterDetailsUseCase(
        getCharacterDetailsUseCaseImpl: GetCharacterDetailsUseCaseImpl
    ) : GetCharacterDetailsUseCase
}