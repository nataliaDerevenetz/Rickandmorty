package com.example.database.models

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["episodeId", "characterId"],
    indices = [Index("characterId")]
)
data class EpisodeCharacterCrossRef(
    val episodeId: Int,
    val characterId: Int
)

