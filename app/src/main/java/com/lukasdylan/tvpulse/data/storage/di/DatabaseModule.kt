package com.lukasdylan.tvpulse.data.storage.di

import android.content.Context
import androidx.room.Room
import com.lukasdylan.tvpulse.data.storage.dao.FavoriteTvShowDao
import com.lukasdylan.tvpulse.data.storage.db.TvPulseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTvPulseDatabase(@ApplicationContext context: Context): TvPulseDatabase =
        Room.databaseBuilder(context, TvPulseDatabase::class.java, "tvpulse.db").build()

    @Provides
    fun provideFavoriteShowDao(database: TvPulseDatabase): FavoriteTvShowDao =
        database.favoriteTvShowDao()
}
