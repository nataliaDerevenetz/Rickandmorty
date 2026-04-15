package com.example.episodes.EpisodeDetails.adapter

import com.example.models.Character

sealed class EpisodeListItem {
    data class Header(val name: String, val airDate: String) : EpisodeListItem()
    data class CharacterItem(val character: Character) : EpisodeListItem()
}