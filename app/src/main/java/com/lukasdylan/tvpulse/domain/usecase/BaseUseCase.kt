package com.lukasdylan.tvpulse.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

data object NoParam
abstract class UseCase<in UseCaseInput, out UseCaseOutput> {
    protected abstract suspend fun execute(input: UseCaseInput): UseCaseResult<UseCaseOutput>

    suspend operator fun invoke(input: UseCaseInput): UseCaseResult<UseCaseOutput> =
        withContext(context = Dispatchers.IO) {
            execute(input = input)
        }
}

suspend operator fun <UseCaseOutput> UseCase<NoParam, UseCaseOutput>.invoke() =
    invoke(input = NoParam)

abstract class AsyncUseCase<in UseCaseInput, out UseCaseOutput> {
    protected abstract suspend fun execute(input: UseCaseInput): Flow<UseCaseResult<UseCaseOutput>>
    suspend operator fun invoke(input: UseCaseInput): Flow<UseCaseResult<UseCaseOutput>> =
        execute(input = input)
            .flowOn(context = Dispatchers.IO)
            .catch { emit(UseCaseResult.GeneralError("")) }
}

suspend operator fun <UseCaseOutput> AsyncUseCase<NoParam, UseCaseOutput>.invoke() =
    invoke(input = NoParam)