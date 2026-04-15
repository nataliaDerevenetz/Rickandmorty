package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.EpisodeRemoteKey

@Entity("EpisodeRemoteKey")
data class EpisodeRemoteKeyEntity(
    @PrimaryKey val episodeId: Int,
    val prevPage: Int?,
    val nextPage: Int?
)

fun EpisodeRemoteKeyEntity.toEpisodeRemoteKey() = EpisodeRemoteKey(
    episodeId = episodeId,
    prevPage = prevPage,
    nextPage = nextPage
)
