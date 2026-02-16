package com.example.tvmaze.data.repository

import com.example.tvmaze.data.Show
import com.example.tvmaze.data.local.FavoriteDao
import com.example.tvmaze.data.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {

    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    fun getFavoriteIds(): Flow<List<Int>> {
        return favoriteDao.getFavoriteIds()
    }

    suspend fun addFavorite(show: Show) {
        val entity = FavoriteEntity(
            showId = show.id,
            name = show.name,
            imageUrl = show.image?.medium ?: show.image?.original
        )
        favoriteDao.insertFavorite(entity)
    }

    suspend fun removeFavorite(showId: Int) {
        favoriteDao.deleteFavorite(showId)
    }
}

