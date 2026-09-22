package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import javax.inject.Inject

class GetTvShowByIdUseCase @Inject constructor(private val repository: TvShowRepository) :
    UseCase<Int, TvShowDetail>() {
    override suspend fun execute(input: Int): UseCaseResult<TvShowDetail> =
        repository.fetchTvShowDetail(id = input).mapToUseCaseResult { it }
}
