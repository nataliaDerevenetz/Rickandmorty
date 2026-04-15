package com.example.network.di

import com.google.gson.annotations.SerializedName

data class CharactersResponse(
    @SerializedName("info")
    val info: InfoResponse,

    @SerializedName("results")
    val results: List<CharacterResponse>,
)

data class CharacterResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("location")
    val location: LocationResponse
)

data class LocationResponse(
    @SerializedName("name")
    val name: String
)

data class InfoResponse(
    @SerializedName("next")
    val next: String?,

    @SerializedName("prev")
    val prev: String?
)
