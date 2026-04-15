package com.example.data.models

import com.example.database.models.CharacterEntity
import com.example.database.models.CharacterFilterEntity
import com.example.models.Character
import com.example.network.di.CharacterResponse
import com.example.network.models.EpisodeCharacterResponse

fun CharacterResponse.toCharacterFilterEntity() =
    CharacterFilterEntity(
        id = id,
        name = name,
        gender = gender,
        image = image,
        location = location.name
    )

fun CharacterFilterEntity.toCharacter() =
    Character(
        id = id,
        name = name,
        gender = gender,
        image = image,
        location = location
    )

fun Character.toCharacterEntity() =
    CharacterEntity(
        id = id,
        name = name,
        gender = gender,
        image = image,
        location = location
    )

fun EpisodeCharacterResponse.toCharacter() =
    Character(
        id = id,
        name = name,
        image = image
    )

fun CharacterEntity.toCharacter() =
    Character(
        id = id,
        name = name,
        gender = gender,
        image = image,
        location = location
    )

fun CharacterResponse.toCharacter() =
    Character(
        id = id,
        name = name,
        gender = gender,
        image = image,
        location = location.name
    )
