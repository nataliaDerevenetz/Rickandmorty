package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val airDate: Long,
)
