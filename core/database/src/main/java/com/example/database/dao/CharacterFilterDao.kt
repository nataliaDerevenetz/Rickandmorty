package com.example.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.models.CharacterFilterEntity
import com.example.models.Character

@Dao
interface CharacterFilterDao {
    @Transaction
    @Query("SELECT * FROM characterFilterEntity ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, CharacterFilterEntity>

    @Query("DELETE FROM characterFilterEntity")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<CharacterFilterEntity>)

}