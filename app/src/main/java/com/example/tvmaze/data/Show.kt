package com.example.tvmaze.data

data class Show(
    val id: Int,
    val name: String,
    val genres: List<String> = emptyList(),
    val image: Image?,
    val summary: String?,
    val episodes: List<Episode>? = emptyList(),   // ✅ nullable
    var isFavorite: Boolean = false
)


data class Image(
    val medium: String?,
    val original: String?
)

data class Episode(
    val id: Int,
    val name: String,
    val season: Int,
    val number: Int,

    val airdate: String? = null,
    val runtime: Int? = null,
    val summary: String? = null,
    val image: Image? = null,
    val rating: Rating? = null
)

data class Rating(
    val average: Double? = null
)
