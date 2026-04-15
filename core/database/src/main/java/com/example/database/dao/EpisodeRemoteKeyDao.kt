package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.EpisodeRemoteKeyEntity

@Dao
interface EpisodeRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<EpisodeRemoteKeyEntity>)

    @Query("SELECT * FROM EpisodeRemoteKey WHERE episodeId = :id")
    suspend fun getRemoteKeysById(id: Int): EpisodeRemoteKeyEntity?

    @Query("DELETE FROM EpisodeRemoteKey")
    suspend fun clearRemoteKeys()
}