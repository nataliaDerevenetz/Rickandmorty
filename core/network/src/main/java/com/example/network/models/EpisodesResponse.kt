package com.example.network.models

import com.example.network.di.InfoResponse
import com.google.gson.annotations.SerializedName

data class EpisodesResponse(
    @SerializedName("info")
    val info: InfoResponse,

    @SerializedName("results")
    val results: List<EpisodeResponse>
)

data class EpisodeResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("air_date")
    val airDate: String,
)