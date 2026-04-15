package com.example.domain.impl

import com.example.domain.GetEpisodeDetailsUseCase
import com.example.domain.repository.ExplorerRepository
import com.example.models.EpisodeDetails
import com.example.models.EpisodeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEpisodeDetailsUseCaseImpl @Inject constructor(
    private val explorerRepository: ExplorerRepository
): GetEpisodeDetailsUseCase {
    override fun invoke(episodeId: Int): Flow<EpisodeDetails> = flow {

        //local
        val episodeDetails = explorerRepository.getEpisodeInfoFromLocal(episodeId)
        if (episodeDetails != null) {
            emit(episodeDetails)
        }

        //remote
        val remoteEpisode = explorerRepository.fetchEpisodeInfoById(episodeId) ?: return@flow
        val ids = remoteEpisode.characters.joinToString(",")
        val remoteCharacters = explorerRepository.fetchCharacterByIds(ids) ?: return@flow
        explorerRepository.saveEpisodeInfo(remoteEpisode, remoteCharacters)
        emit(EpisodeDetails(
            EpisodeInfo(id = remoteEpisode.id, name = remoteEpisode.name, airDate = remoteEpisode.airDate),
            remoteCharacters))
    }
}