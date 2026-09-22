package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import javax.inject.Inject

class AddFavoriteTvShowUseCase @Inject constructor(private val repository: TvShowRepository) :
    UseCase<TvShowDetail, Unit>() {
    override suspend fun execute(input: TvShowDetail): UseCaseResult<Unit> =
        UseCaseResult.Success(repository.addFavorite(show = input))
}
