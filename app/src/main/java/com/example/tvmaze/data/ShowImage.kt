package com.example.tvmaze.data

data class ShowImage(
    val id: Int,
    val type: String?,      // poster, banner, background vs.
    val main: Boolean?,
    val resolutions: ImageResolutions
)

data class ImageResolutions(
    val medium: ImageSize?,
    val original: ImageSize?
)

data class ImageSize(
    val url: String
)
