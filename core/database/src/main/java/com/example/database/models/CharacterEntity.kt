package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CharacterEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val image: String,
    @ColumnInfo val gender: String,
    @ColumnInfo val location: String
)
