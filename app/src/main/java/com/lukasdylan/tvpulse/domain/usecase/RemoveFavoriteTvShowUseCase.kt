package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import javax.inject.Inject

class RemoveFavoriteTvShowUseCase @Inject constructor(private val repository: TvShowRepository) :
    UseCase<Int, Unit>() {
    override suspend fun execute(input: Int): UseCaseResult<Unit> =
        UseCaseResult.Success(repository.removeFavorite(id = input))
}
