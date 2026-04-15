package com.example.models

data class EpisodeInfoNetwork(
    val id: Int,
    val name: String,
    val airDate: String,
    val characters: List<Int>
)
