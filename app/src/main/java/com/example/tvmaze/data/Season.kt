package com.example.tvmaze.data

data class Season(
    val id: Int,
    val number: Int?,
    val episodeOrder: Int?,
    val premiereDate: String?,
    val endDate: String?,
    val image: Image?
)