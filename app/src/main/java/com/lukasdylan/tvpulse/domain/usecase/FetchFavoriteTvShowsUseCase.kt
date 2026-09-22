package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchFavoriteTvShowsUseCase @Inject constructor(private val repository: TvShowRepository) :
    AsyncUseCase<NoParam, List<TvShow>>() {
    override suspend fun execute(input: NoParam): Flow<UseCaseResult<List<TvShow>>> =
        repository.observeFavoriteTvShows().mapToUseCaseResult { it }
}
