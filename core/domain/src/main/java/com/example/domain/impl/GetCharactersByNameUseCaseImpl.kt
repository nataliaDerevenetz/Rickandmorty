package com.example.domain.impl

import com.example.domain.GetCharactersByNameUseCase
import com.example.domain.repository.ExplorerRepository
import javax.inject.Inject

class GetCharactersByNameUseCaseImpl @Inject constructor(
    private val explorerRepository: ExplorerRepository
): GetCharactersByNameUseCase {
    override operator fun invoke(name: String) = explorerRepository.getCharactersByNameFlow(name)
}
