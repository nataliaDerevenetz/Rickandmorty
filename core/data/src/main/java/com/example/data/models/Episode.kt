package com.example.data.models

import com.example.database.models.EpisodeEntity
import com.example.models.Episode
import com.example.network.models.EpisodeResponse
import com.example.utils.timestampToString
import com.example.utils.toTimestamp

fun EpisodeResponse.toEpisodeEntity() =
    EpisodeEntity(
        id = id,
        name = name,
        airDate = airDate.toTimestamp("MMMM d, yyyy")
    )


fun EpisodeEntity.toEpisode() =
    Episode(
        id = id,
        name = name,
        airDate = airDate.timestampToString()
    )