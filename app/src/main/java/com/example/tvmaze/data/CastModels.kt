package com.example.tvmaze.data

data class CastItem(
    val person: Person,
    val character: Character,
    val self: Boolean? = null,
    val voice: Boolean? = null
)

data class Person(
    val id: Int,
    val name: String,
    val image: Image? = null
)

data class Character(
    val id: Int,
    val name: String,
    val image: Image? = null
)
