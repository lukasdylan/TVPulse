package com.lukasdylan.tvpulse.data.repository.di

import com.lukasdylan.tvpulse.data.repository.TvShowRepositoryImpl
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindShowRepository(impl: TvShowRepositoryImpl): TvShowRepository
}
