package com.example.data.local

import androidx.paging.PagingSource
import com.example.database.models.CharacterFilterEntity
import com.example.database.models.CharacterRemoteKeyEntity
import com.example.database.models.EpisodeEntity
import com.example.database.models.EpisodeRemoteKeyEntity
import com.example.models.Character
import com.example.models.EpisodeDetails
import com.example.models.EpisodeInfo

interface DBStorage {
    fun getCharactersFilter(): PagingSource<Int, CharacterFilterEntity>
    suspend fun clearRemoteKeys()
    suspend fun insertCharacterFilterTransaction(characters: List<CharacterFilterEntity>, remoteKeys: List<CharacterRemoteKeyEntity>, isClearDB: Boolean)
    suspend fun getRemoteKeyByCharacterId(id: Int): CharacterRemoteKeyEntity?

    fun getEpisodes(): PagingSource<Int, EpisodeEntity>
    suspend fun clearEpisodeRemoteKeys()
    suspend fun insertEpisodesTransaction(episodes: List<EpisodeEntity>, remoteKeys: List<EpisodeRemoteKeyEntity>, isClearDB: Boolean)
    suspend fun getEpisodeRemoteKeyByCharacterId(id: Int): EpisodeRemoteKeyEntity?

    suspend fun saveEpisodeInfo(episodeInfo: EpisodeInfo,characters: List<Character>)
    suspend fun getEpisodeFullInfoById(episodeId :Int): EpisodeDetails?

    suspend fun saveCharacterDetail(character: Character)
    suspend fun getCharacterFullInfoById(characterId: Int): Character?
}