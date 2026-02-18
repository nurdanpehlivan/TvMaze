package com.example.tvmaze.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmaze.data.CastItem
import com.example.tvmaze.data.Episode
import com.example.tvmaze.data.Show
import com.example.tvmaze.network.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.tvmaze.data.CrewItemModel
import com.example.tvmaze.data.ShowImage
import com.example.tvmaze.data.Season
import com.example.tvmaze.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class ShowViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    // ---------- Main lists ----------
    private var allShows: List<Show> = emptyList()

    var shows by mutableStateOf<List<Show>>(emptyList())
        private set

    var allGenres by mutableStateOf<List<String>>(emptyList())
        private set

    var favoriteShows by mutableStateOf<List<Show>>(emptyList())
        private set

    var selectedGenre by mutableStateOf<String?>(null)
        private set

    var hasData by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // ---------- Detail states ----------
    var detail by mutableStateOf<Show?>(null)
        private set

    var episodes by mutableStateOf<List<Episode>>(emptyList())
        private set

    var cast by mutableStateOf<List<CastItem>>(emptyList())
        private set
    var seasons by mutableStateOf<List<Season>>(emptyList())
        private set

    var crew by mutableStateOf<List<CrewItemModel>>(emptyList())
        private set

    var images by mutableStateOf<List<ShowImage>>(emptyList())
        private set

    // Favori ID'leri için set
    private var favoriteIds = setOf<Int>()

    init {
        // Uygulama başladığında veritabanından favorileri yükle
        loadFavoritesFromDb()
    }

    // ---------- Favorites ----------
    private fun loadFavoritesFromDb() {
        viewModelScope.launch {
            try {
                favoriteRepository.getFavoriteIds().collect { ids ->
                    favoriteIds = ids.toSet()

                    // Mevcut listeyi güncelle
                    allShows = allShows.map { show ->
                        show.copy(isFavorite = show.id in favoriteIds)
                    }
                    shows = shows.map { show ->
                        show.copy(isFavorite = show.id in favoriteIds)
                    }
                    detail = detail?.copy(isFavorite = detail?.id in favoriteIds)

                    // Favori listesini güncelle
                    updateFavoriteShows()
                }
            } catch (e: Exception) {
                Log.e("ShowViewModel", "loadFavoritesFromDb failed", e)
            }
        }
    }

    private suspend fun updateFavoriteShows() {
        try {
            val favorites = favoriteRepository.getAllFavorites().firstOrNull() ?: emptyList()

            // FavoriteEntity'den Show'a dönüştür
            favoriteShows = favorites.map { entity ->
                // Önce mevcut listede ara
                allShows.find { it.id == entity.showId }
                    ?: Show(
                        id = entity.showId,
                        name = entity.name,
                        genres = emptyList(),
                        image = entity.imageUrl?.let {
                            com.example.tvmaze.data.Image(medium = it, original = it)
                        },
                        summary = null,
                        episodes = emptyList(),
                        isFavorite = true
                    )
            }
        } catch (e: Exception) {
            Log.e("ShowViewModel", "updateFavoriteShows failed", e)
        }
    }

    fun toggleFavorite(show: Show) {
        viewModelScope.launch {
            try {
                val newFav = !show.isFavorite

                if (newFav) {
                    // Favorilere ekle
                    favoriteRepository.addFavorite(show)
                } else {
                    // Favorilerden çıkar
                    favoriteRepository.removeFavorite(show.id)
                }

                // Local state'i güncelle
                allShows = allShows.map { s ->
                    if (s.id == show.id) s.copy(isFavorite = newFav) else s
                }

                shows = shows.map { s ->
                    if (s.id == show.id) s.copy(isFavorite = newFav) else s
                }

                // Eğer detail ekranda bu show gösteriliyorsa, onu da güncelle
                detail = detail?.let { d ->
                    if (d.id == show.id) d.copy(isFavorite = newFav) else d
                }

                // Favori listesini güncelle
                updateFavoriteShows()
            } catch (e: Exception) {
                Log.e("ShowViewModel", "toggleFavorite failed", e)
            }
        }
    }

    fun getShowById(id: Int): Show? = allShows.find { it.id == id }

    // ---------- List fetch ----------
    fun getPopularShows() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = RetrofitInstance.api.getShows(page = 0)

                // Favorileri koru
                val merged = result.map { it.copy(isFavorite = it.id in favoriteIds) }

                allShows = merged
                selectedGenre = null
                shows = merged
                updateGenres(merged)
                updateFavoriteShows()
                hasData = true
            } catch (e: Exception) {
                Log.e("ShowViewModel", "getPopularShows failed", e)
                errorMessage = e.message ?: "Popüler diziler alınamadı"
            } finally {
                isLoading = false
            }
        }
    }

    fun searchShow(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                if (query.isBlank()) {
                    errorMessage = "Arama metni boş olamaz"
                    return@launch
                }

                val response = RetrofitInstance.api.searchShows(query)
                val list = response.map { it.show }

                // Favorileri koru
                val merged = list.map { it.copy(isFavorite = it.id in favoriteIds) }

                allShows = merged
                selectedGenre = null
                shows = merged
                updateGenres(merged)
                updateFavoriteShows()
                hasData = true
            } catch (e: Exception) {
                Log.e("ShowViewModel", "searchShow failed", e)
                errorMessage = e.message ?: "Arama başarısız"
            } finally {
                isLoading = false
            }
        }
    }

    private fun updateGenres(list: List<Show>) {
        allGenres = list.flatMap { it.genres }.distinct()
    }

    fun filterByGenre(genre: String?) {
        selectedGenre = genre
        shows = if (genre == null) allShows else allShows.filter { it.genres.contains(genre) }
    }

    // ---------- Detail fetch ----------
    fun loadDetail(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val showDetail = RetrofitInstance.api.getShowDetail(id)
                // Favori durumunu koru
                detail = showDetail.copy(isFavorite = id in favoriteIds)

                episodes = RetrofitInstance.api.getEpisodes(id)
                seasons = RetrofitInstance.api.getSeasons(id)
                cast = RetrofitInstance.api.getCast(id)
                crew = RetrofitInstance.api.getCrew(id)
                images = RetrofitInstance.api.getImages(id)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Detay yüklenemedi"
            } finally {
                isLoading = false
            }
        }
    }

}
