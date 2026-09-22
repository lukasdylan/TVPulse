package com.lukasdylan.tvpulse.data.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lukasdylan.tvpulse.data.storage.dao.FavoriteTvShowDao
import com.lukasdylan.tvpulse.data.storage.entity.FavoriteTvShowEntity
import com.lukasdylan.tvpulse.data.storage.utils.GenreConverter

@Database(entities = [FavoriteTvShowEntity::class], version = 1, exportSchema = false)
@TypeConverters(GenreConverter::class)
abstract class TvPulseDatabase : RoomDatabase() {
    abstract fun favoriteTvShowDao(): FavoriteTvShowDao
}