package com.example.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.models.EpisodeEntity

@Dao
interface EpisodeDao {
    @Transaction
    @Query("SELECT * FROM episodeEntity ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, EpisodeEntity>

    @Query("DELETE FROM episodeEntity")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<EpisodeEntity>)
}