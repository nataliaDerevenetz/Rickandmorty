package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.CharacterRemoteKey

@Entity("RemoteKey")
data class CharacterRemoteKeyEntity(
    @PrimaryKey val characterId: Int,
    val prevPage: Int?,
    val nextPage: Int?
)

fun CharacterRemoteKeyEntity.toRemoteKey() = CharacterRemoteKey(
    characterId = characterId,
    prevPage = prevPage,
    nextPage = nextPage
)