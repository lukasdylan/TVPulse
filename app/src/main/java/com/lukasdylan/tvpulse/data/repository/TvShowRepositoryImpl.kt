package com.lukasdylan.tvpulse.data.repository

import com.lukasdylan.tvpulse.data.remote.service.TvMazeApiService
import com.lukasdylan.tvpulse.data.storage.dao.FavoriteTvShowDao
import com.lukasdylan.tvpulse.data.storage.entity.FavoriteTvShowEntity
import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TvShowRepositoryImpl @Inject constructor(
    private val apiService: TvMazeApiService,
    private val dao: FavoriteTvShowDao
) : TvShowRepository {
    override suspend fun fetchTvShows(): Result<List<TvShow>> = runCatching {
        apiService.getTvShows().take(30).map {
            TvShow(
                id = it.id ?: 0,
                name = it.name.orEmpty(),
                imageUrl = (it.image?.medium ?: it.image?.original).orEmpty(),
                genres = it.genres.orEmpty(),
                rating = it.rating?.average ?: 0.0
            )
        }
    }

    override suspend fun searchTvShowsByText(text: String): Result<List<TvShow>> = runCatching {
        apiService.searchTvShows(query = text).map {
            TvShow(
                id = it.show?.id ?: 0,
                name = it.show?.name.orEmpty(),
                imageUrl = (it.show?.image?.medium ?: it.show?.image?.original).orEmpty(),
                genres = it.show?.genres.orEmpty(),
                rating = it.show?.rating?.average ?: 0.0
            )
        }
    }

    override suspend fun fetchTvShowDetail(id: Int): Result<TvShowDetail> = runCatching {
        val result = apiService.getTvShowDetail(id = id)
        TvShowDetail(
            id = result.id ?: 0,
            name = result.name.orEmpty(),
            imageUrl = (result.image?.medium ?: result.image?.original).orEmpty(),
            genres = result.genres.orEmpty(),
            rating = result.rating?.average ?: 0.0,
            synopsis = result.summary.orEmpty()
        )
    }

    override fun observeFavoriteTvShows(): Flow<List<TvShow>> =
        dao.observeFavoriteTvShows().map { shows ->
            shows.map {
                TvShow(
                    id = it.id,
                    name = it.name,
                    imageUrl = it.imageUrl,
                    genres = it.genres,
                    rating = it.rating
                )
            }
        }

    override fun observeIsFavorite(id: Int): Flow<Boolean> = dao.observeIsFavorite(id = id)

    override suspend fun addFavorite(show: TvShowDetail) {
        try {
            dao.insert(
                entity = FavoriteTvShowEntity(
                    id = show.id,
                    name = show.name,
                    imageUrl = show.imageUrl,
                    genres = show.genres,
                    rating = show.rating,
                    createdDate = System.currentTimeMillis()
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override suspend fun removeFavorite(id: Int) {
        try {
            dao.deleteById(id = id)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}