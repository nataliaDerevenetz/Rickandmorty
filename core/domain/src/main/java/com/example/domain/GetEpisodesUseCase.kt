package com.example.domain

import androidx.paging.PagingData
import com.example.models.Episode
import kotlinx.coroutines.flow.Flow

interface GetEpisodesUseCase {
    operator fun invoke(): Flow<PagingData<Episode>>
}