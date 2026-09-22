package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import javax.inject.Inject

class SearchTvShowsUseCase @Inject constructor(private val repository: TvShowRepository) :
    UseCase<String, List<TvShow>>() {
    override suspend fun execute(input: String): UseCaseResult<List<TvShow>> =
        repository.searchTvShowsByText(text = input).mapToUseCaseResult { it }
}
