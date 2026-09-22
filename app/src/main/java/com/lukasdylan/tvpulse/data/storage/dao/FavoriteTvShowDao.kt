package com.lukasdylan.tvpulse.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lukasdylan.tvpulse.data.storage.entity.FavoriteTvShowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTvShowDao {
    @Query("SELECT * FROM favorite_tv_show ORDER BY createdDate DESC")
    fun observeFavoriteTvShows(): Flow<List<FavoriteTvShowEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tv_show WHERE id = :id)")
    fun observeIsFavorite(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteTvShowEntity)

    @Query("DELETE FROM favorite_tv_show WHERE id = :id")
    suspend fun deleteById(id: Int)
}