package com.example.domain.repository

import androidx.paging.PagingData
import com.example.models.Character
import com.example.models.Episode
import com.example.models.EpisodeDetails
import com.example.models.EpisodeInfoNetwork
import kotlinx.coroutines.flow.Flow

interface ExplorerRepository {
    fun getCharactersByNameFlow(name: String): Flow<PagingData<Character>>

    fun getEpisodesFlow(): Flow<PagingData<Episode>>

    suspend fun fetchEpisodeInfoById(episodeId: Int): EpisodeInfoNetwork?
    suspend fun fetchCharacterByIds(ids: String): List<Character>?
    suspend fun saveEpisodeInfo(episodeInfo: EpisodeInfoNetwork, characters: List<Character>)
    suspend fun getEpisodeInfoFromLocal(episodeId: Int): EpisodeDetails?

    suspend fun fetchCharacterById(characterId: Int):Character?
    suspend fun saveCharacter(character: Character)
    suspend fun getCharacterFromLocal(characterId: Int): Character?
}