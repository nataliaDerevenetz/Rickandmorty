package com.example.domain

import com.example.models.EpisodeDetails
import kotlinx.coroutines.flow.Flow

interface GetEpisodeDetailsUseCase {
    operator fun invoke(episodeId: Int): Flow<EpisodeDetails>
}