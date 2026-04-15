package com.example.domain

import androidx.paging.PagingData
import com.example.models.Character
import kotlinx.coroutines.flow.Flow

interface GetCharactersByNameUseCase {
    operator fun invoke(name: String): Flow<PagingData<Character>>
}