package com.example.network.models

import com.google.gson.annotations.SerializedName

data class EpisodeCharacterResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val image: String,

)
