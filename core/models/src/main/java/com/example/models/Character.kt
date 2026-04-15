package com.example.models

data class Character(
    val id: Int,
    val name: String,
    val gender: String = "",
    val image: String,
    val location: String = ""
)