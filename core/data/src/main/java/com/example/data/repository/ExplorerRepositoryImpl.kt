package com.example.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.local.DBStorage
import com.example.data.models.toCharacter
import com.example.data.models.toEpisode
import com.example.data.models.toEpisodeInfo
import com.example.data.models.toEpisodeInfoNetwork
import com.example.data.remote.CharactersFilterRemoteMediator
import com.example.data.remote.EpisodesRemoteMediator
import com.example.domain.repository.ExplorerRepository
import com.example.models.Character
import com.example.models.Episode
import com.example.models.EpisodeDetails
import com.example.models.EpisodeInfoNetwork
import com.example.network.di.ExplorerService
import com.example.network.NetworkHelper
import com.example.utils.Constant.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExplorerRepositoryImpl @Inject constructor(
    private val dbStorage : DBStorage,
    private val explorerService: ExplorerService,
    private val networkHelper: NetworkHelper
): ExplorerRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getCharactersByNameFlow(name: String): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = 10
            ),
            remoteMediator = CharactersFilterRemoteMediator(
                explorerService = explorerService,
                name = name,
                dbStorage = dbStorage,
                networkHelper = networkHelper
            ),
            pagingSourceFactory = {
                dbStorage.getCharactersFilter()
            }
        ).flow.map { pagingData -> pagingData.map { it.toCharacter() } }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getEpisodesFlow(): Flow<PagingData<Episode>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = 10
            ),
            remoteMediator = EpisodesRemoteMediator(
                explorerService = explorerService,
                dbStorage = dbStorage,
                networkHelper = networkHelper
            ),
            pagingSourceFactory = {
                dbStorage.getEpisodes()
            }
        ).flow.map { pagingData -> pagingData.map { it.toEpisode() } }
    }

    override suspend fun fetchEpisodeInfoById(episodeId: Int): EpisodeInfoNetwork? {
        val response = explorerService.getEpisodeInfoById(episodeId)
        val episodeInfoNetwork = if (response.isSuccessful) { response.body()?.toEpisodeInfoNetwork() } else null
        return episodeInfoNetwork
    }

    override suspend fun getEpisodeInfoFromLocal(episodeId: Int): EpisodeDetails? {
        return dbStorage.getEpisodeFullInfoById(episodeId)
    }

    override suspend fun fetchCharacterById(characterId: Int): Character? {
        val response = explorerService.getCharacterById(characterId)
        val character = if (response.isSuccessful) { response.body()?.toCharacter() } else null
        return character
    }

    override suspend fun saveCharacter(character: Character) {
        return dbStorage.saveCharacterDetail(character)
    }

    override suspend fun getCharacterFromLocal(characterId: Int): Character? {
        return dbStorage.getCharacterFullInfoById(characterId)
    }

    override suspend fun fetchCharacterByIds(ids: String): List<Character>? {
        val response = explorerService.getCharacterByIds(ids)
        val characters = if (response.isSuccessful) { response.body()?.map { it.toCharacter() } } else null
        return characters
    }

    override suspend fun saveEpisodeInfo(
        episodeInfo: EpisodeInfoNetwork,
        characters: List<Character>
    ) {
        return dbStorage.saveEpisodeInfo(episodeInfo.toEpisodeInfo(),characters)
    }

}