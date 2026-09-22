package com.lukasdylan.tvpulse.domain.repository

import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import kotlinx.coroutines.flow.Flow

interface TvShowRepository {
    suspend fun fetchTvShows(): Result<List<TvShow>>

    suspend fun searchTvShowsByText(text: String): Result<List<TvShow>>

    suspend fun fetchTvShowDetail(id: Int): Result<TvShowDetail>

    fun observeFavoriteTvShows(): Flow<List<TvShow>>

    fun observeIsFavorite(id: Int): Flow<Boolean>

    suspend fun addFavorite(show: TvShowDetail)

    suspend fun removeFavorite(id: Int)
}