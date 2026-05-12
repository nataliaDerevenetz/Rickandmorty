package com.example.domain.impl

import com.example.domain.GetCharacterDetailsUseCase
import com.example.domain.repository.ExplorerRepository
import com.example.models.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCharacterDetailsUseCaseImpl @Inject constructor(
    private val explorerRepository: ExplorerRepository
): GetCharacterDetailsUseCase {
    override fun invoke(characterId: Int): Flow<Character> = flow {
        //local
        val localCharacter = explorerRepository.getCharacterFromLocal(characterId)
        if (localCharacter != null) {
            emit(localCharacter)
        }

        //remote
        val remoteCharacter = explorerRepository.fetchCharacterById(characterId) ?: return@flow
        explorerRepository.saveCharacter(remoteCharacter)
        emit(remoteCharacter)
    }
}