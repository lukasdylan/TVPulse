package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteStateUseCase @Inject constructor(private val repository: TvShowRepository) :
    AsyncUseCase<Int, Boolean>() {
    override suspend fun execute(input: Int): Flow<UseCaseResult<Boolean>> =
        repository.observeIsFavorite(id = input).mapToUseCaseResult { it }
}
