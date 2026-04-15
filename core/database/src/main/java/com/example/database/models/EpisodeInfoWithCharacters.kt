package com.example.database.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EpisodeInfoWithCharacters(
    @Embedded val episode: EpisodeInfoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EpisodeCharacterCrossRef::class,
            parentColumn = "episodeId",
            entityColumn = "characterId"
        )
    )
    val characters: List<CharacterEntity>
)
