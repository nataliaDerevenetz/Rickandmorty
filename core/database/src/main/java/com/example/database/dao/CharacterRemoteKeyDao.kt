package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.models.CharacterRemoteKeyEntity

@Dao
interface CharacterRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<CharacterRemoteKeyEntity>)

    @Query("SELECT * FROM RemoteKey WHERE characterId = :id")
    suspend fun getRemoteKeysById(id: Int): CharacterRemoteKeyEntity?

    @Query("DELETE FROM RemoteKey")
    suspend fun clearRemoteKeys()
}
