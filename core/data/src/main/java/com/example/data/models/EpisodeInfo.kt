package com.example.data.models

import com.example.database.models.EpisodeInfoEntity
import com.example.models.EpisodeInfo
import com.example.models.EpisodeInfoNetwork
import com.example.network.models.EpisodeInfoResponse
import com.example.utils.timestampToString
import com.example.utils.toTimestamp

fun EpisodeInfo.toEpisodeInfoEntity() =
    EpisodeInfoEntity(
        id = id,
        name = name,
        airDate = airDate.toTimestamp("dd.MM.yyyy")
    )

fun EpisodeInfoResponse.toEpisodeInfoNetwork() =
    EpisodeInfoNetwork(
        id = id,
        name = name,
        airDate = airDate.toTimestamp("MMMM d, yyyy").timestampToString(),
        characters = characters.map { it.substringAfterLast("/").toInt() }
    )

fun EpisodeInfoNetwork.toEpisodeInfo() =
    EpisodeInfo(
        id = id,
        name = name,
        airDate = airDate
    )

fun EpisodeInfoEntity.toEpisodeInfo() =
    EpisodeInfo(
        id = id,
        name = name,
        airDate = airDate.timestampToString()
    )
