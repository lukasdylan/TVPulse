package com.lukasdylan.tvpulse.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.IOException

sealed class UseCaseResult<out T> {
    data class Success<out T>(val data: T) : UseCaseResult<T>()
    data object ConnectionError : UseCaseResult<Nothing>()
    data class GeneralError(val errorReason: String) : UseCaseResult<Nothing>()
}

fun <T, R> Result<T>.mapToUseCaseResult(block: (T) -> R): UseCaseResult<R> = this.fold(
    onSuccess = { result -> UseCaseResult.Success(block(result)) },
    onFailure = { throwable ->
        when (throwable) {
            is IOException -> UseCaseResult.ConnectionError
            is retrofit2.HttpException -> UseCaseResult.GeneralError("Something went wrong (code: ${throwable.code()} - ${throwable.message()}). Please try again.")
            else -> UseCaseResult.GeneralError("Unexpected error occurred. Please try again.")
        }
    }
)

fun <T, R> Flow<T>.mapToUseCaseResult(block: (T) -> R): Flow<UseCaseResult<R>> = map {
    UseCaseResult.Success(block(it))
}