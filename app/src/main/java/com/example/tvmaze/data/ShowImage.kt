package com.example.tvmaze.data


data class ShowImage(
    val id: Int,
    val type: String? = null,
    val main: Boolean? = null,
    val resolutions: Resolutions
)

data class Resolutions(
    val medium: ImageUrl? = null,
    val original: ImageUrl? = null
)

data class ImageUrl(
    val url: String
)