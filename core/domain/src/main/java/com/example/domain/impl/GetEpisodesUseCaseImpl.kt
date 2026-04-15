package com.example.domain.impl

import com.example.domain.GetEpisodesUseCase
import com.example.domain.repository.ExplorerRepository
import javax.inject.Inject

class GetEpisodesUseCaseImpl @Inject constructor(
    private val explorerRepository: ExplorerRepository
): GetEpisodesUseCase {
    override operator fun invoke() = explorerRepository.getEpisodesFlow()
}