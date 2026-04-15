package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.models.CharacterEntity
import com.example.database.models.EpisodeCharacterCrossRef
import com.example.database.models.EpisodeInfoEntity
import com.example.database.models.EpisodeInfoWithCharacters

@Dao
interface EpisodeInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeInfoEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharactersIfNotExist(characters: List<CharacterEntity>)

    @Query("UPDATE characterEntity SET name = :name, image = :image WHERE id = :id")
    suspend fun updateCharacterBasicInfo(id: Int, name: String, image: String)

    @Query("DELETE FROM episodeCharacterCrossRef WHERE episodeId = :episodeId")
    suspend fun deleteEpisodeCharacters(episodeId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodeCharacterLinks(links: List<EpisodeCharacterCrossRef>)

    @Transaction
    suspend fun saveEpisodeWithCharacters(
        episode: EpisodeInfoEntity,
        characters: List<CharacterEntity>
    ) {
        insertEpisode(episode)
        insertCharactersIfNotExist(characters)
        characters.forEach { char ->
            updateCharacterBasicInfo(char.id, char.name, char.image)
        }
       deleteEpisodeCharacters(episode.id)
        val links = characters.map { EpisodeCharacterCrossRef(episode.id, it.id) }
        insertEpisodeCharacterLinks(links)
    }

    @Transaction
    @Query("SELECT * FROM episodeInfoEntity WHERE id = :episodeId")
    suspend fun getEpisodeWithCharactersById(episodeId: Int): EpisodeInfoWithCharacters?
}