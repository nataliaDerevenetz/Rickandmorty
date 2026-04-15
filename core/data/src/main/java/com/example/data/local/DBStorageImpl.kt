package com.example.data.local

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.example.data.models.toCharacter
import com.example.data.models.toCharacterEntity
import com.example.data.models.toEpisodeInfo
import com.example.data.models.toEpisodeInfoEntity
import com.example.database.dao.CharacterDao
import com.example.database.dao.CharacterFilterDao
import com.example.database.dao.CharacterRemoteKeyDao
import com.example.database.dao.EpisodeDao
import com.example.database.dao.EpisodeInfoDao
import com.example.database.dao.EpisodeRemoteKeyDao
import com.example.database.db.ExplorerRoomDatabase
import com.example.database.models.CharacterFilterEntity
import com.example.database.models.CharacterRemoteKeyEntity
import com.example.database.models.EpisodeEntity
import com.example.database.models.EpisodeRemoteKeyEntity
import com.example.models.Character
import com.example.models.EpisodeDetails
import com.example.models.EpisodeInfo
import javax.inject.Inject

class DBStorageImpl @Inject constructor(
    private val explorerRoomDatabase: ExplorerRoomDatabase,
    private val characterFilterDao: CharacterFilterDao,
    private val characterRemoteKeyDao: CharacterRemoteKeyDao,
    private val episodeDao: EpisodeDao,
    private val episodeRemoteKeyDao: EpisodeRemoteKeyDao,
    private val episodeInfoDao: EpisodeInfoDao,
    private val characterDao: CharacterDao,
   ): DBStorage {

    override fun getCharactersFilter(): PagingSource<Int, CharacterFilterEntity> {
        return  characterFilterDao.pagingSource()
    }

    override suspend fun getRemoteKeyByCharacterId(id: Int): CharacterRemoteKeyEntity? {
        return characterRemoteKeyDao.getRemoteKeysById(id)
    }

    override fun getEpisodes(): PagingSource<Int, EpisodeEntity> {
        return  episodeDao.pagingSource()
    }

    override suspend fun clearEpisodeRemoteKeys() {
        episodeRemoteKeyDao.clearRemoteKeys()
    }

    override suspend fun insertEpisodesTransaction(
        episodes: List<EpisodeEntity>,
        remoteKeys: List<EpisodeRemoteKeyEntity>,
        isClearDB: Boolean
    ) {
        explorerRoomDatabase.withTransaction {
            if (isClearDB) {
                episodeDao.clearAll()
                episodeRemoteKeyDao.clearRemoteKeys()
            }
            episodeDao.insertAll(episodes)
            episodeRemoteKeyDao.insertAll(remoteKeys)
        }
    }

    override suspend fun getEpisodeRemoteKeyByCharacterId(id: Int): EpisodeRemoteKeyEntity? {
        return episodeRemoteKeyDao.getRemoteKeysById(id)
    }

    override suspend fun saveEpisodeInfo(episodeInfo: EpisodeInfo, characters: List<Character>) {
        episodeInfoDao.saveEpisodeWithCharacters(episodeInfo.toEpisodeInfoEntity(), characters.map{
            it.toCharacterEntity()
        })
    }

    override suspend fun getEpisodeFullInfoById(episodeId: Int): EpisodeDetails? {
        val episodeInfoWithCharacters = episodeInfoDao.getEpisodeWithCharactersById(episodeId)
        return if (episodeInfoWithCharacters != null)
            EpisodeDetails(
                episode = episodeInfoWithCharacters.episode.toEpisodeInfo(),
                characters = episodeInfoWithCharacters.characters.map {
                    it.toCharacter()
                }) else null
    }

    override suspend fun saveCharacterDetail(character: Character) {
        characterDao.insert(character.toCharacterEntity())
    }

    override suspend fun getCharacterFullInfoById(characterId: Int): Character? {
        return characterDao.getCharactersById(characterId)?.toCharacter()
    }

    override suspend fun clearRemoteKeys() {
        characterRemoteKeyDao.clearRemoteKeys()
    }

    override suspend fun insertCharacterFilterTransaction(
        characters: List<CharacterFilterEntity>,
        remoteKeys: List<CharacterRemoteKeyEntity>,
        isClearDB: Boolean
    ) {
        explorerRoomDatabase.withTransaction {
            if (isClearDB) {
                characterFilterDao.clearAll()
                characterRemoteKeyDao.clearRemoteKeys()
            }
            characterFilterDao.insertAll(characters)
            characterRemoteKeyDao.insertAll(remoteKeys)
        }
    }



}