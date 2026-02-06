package com.example.tvmaze.network

import com.example.tvmaze.data.Show
import com.example.tvmaze.data.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.tvmaze.data.Episode
import com.example.tvmaze.data.CastItem
import com.example.tvmaze.data.CrewItemModel
import com.example.tvmaze.data.Season
import com.example.tvmaze.data.ShowImage




interface TvMazeApi {

    // Tüm dizileri sayfalı getirir (TVmaze mantığı)
    @GET("shows")
    suspend fun getShows(
        @Query("page") page: Int
    ): List<Show>

    // Arama endpoint'i
    @GET("search/shows")
    suspend fun searchShows(
        @Query("q") query: String
    ): List<SearchResponse>

    @GET("shows/{id}")
    suspend fun getShowDetail(@Path("id") id: Int): Show

    @GET("shows/{id}/episodes")
    suspend fun getEpisodes(@Path("id") id: Int): List<Episode>

    @GET("shows/{id}/seasons")
    suspend fun getSeasons(@Path("id") id: Int): List<Season>

    @GET("shows/{id}/cast")
    suspend fun getCast(@Path("id") id: Int): List<CastItem>

    @GET("shows/{id}/crew")
    suspend fun getCrew(@Path("id") id: Int): List<CrewItemModel>

    @GET("shows/{id}/images")
    suspend fun getImages(@Path("id") id: Int): List<ShowImage>
}
