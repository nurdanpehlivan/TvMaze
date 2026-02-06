package com.example.tvmaze.data

data class Season(
    val id: Int,
    val number: Int? = null,
    val episodeOrder: Int? = null,
    val premiereDate: String? = null,
    val endDate: String? = null,
    val image: Image? = null
)
