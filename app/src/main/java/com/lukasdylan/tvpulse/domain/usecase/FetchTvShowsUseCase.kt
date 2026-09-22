package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import javax.inject.Inject

class FetchTvShowsUseCase @Inject constructor(private val repository: TvShowRepository) :
    UseCase<NoParam, List<TvShow>>() {
    override suspend fun execute(input: NoParam): UseCaseResult<List<TvShow>> =
        repository.fetchTvShows().mapToUseCaseResult { it }
}
