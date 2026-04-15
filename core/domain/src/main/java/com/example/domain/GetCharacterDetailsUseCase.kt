package com.example.domain

import com.example.models.Character
import kotlinx.coroutines.flow.Flow

interface GetCharacterDetailsUseCase {
    operator fun invoke(characterId: Int): Flow<Character>
}