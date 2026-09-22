package com.lukasdylan.tvpulse.domain.usecase

import app.cash.turbine.test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class UseCaseResultTest {

    @Test
    fun `mapToUseCaseResult wraps a successful Result in Success`() {
        val result: Result<Int> = Result.success(42)

        val useCaseResult = result.mapToUseCaseResult { it * 2 }

        assertEquals(UseCaseResult.Success(84), useCaseResult)
    }

    @Test
    fun `mapToUseCaseResult maps IOException to ConnectionError`() {
        val result: Result<Int> = Result.failure(IOException("no network"))

        val useCaseResult = result.mapToUseCaseResult { it }

        assertEquals(UseCaseResult.ConnectionError, useCaseResult)
    }

    @Test
    fun `mapToUseCaseResult maps HttpException to a GeneralError containing the response code`() {
        val response = Response.error<Any>(404, "".toResponseBody(null))
        val result: Result<Int> = Result.failure(HttpException(response))

        val useCaseResult = result.mapToUseCaseResult { it } as UseCaseResult.GeneralError

        assertTrue(useCaseResult.errorReason.contains("404"))
    }

    @Test
    fun `mapToUseCaseResult maps unknown exceptions to a generic GeneralError`() {
        val result: Result<Int> = Result.failure(IllegalStateException("boom"))

        val useCaseResult = result.mapToUseCaseResult { it }

        assertEquals(
            UseCaseResult.GeneralError("Unexpected error occurred. Please try again."),
            useCaseResult
        )
    }

    @Test
    fun `Flow mapToUseCaseResult wraps every emission in Success`() = runTest {
        flowOf(1, 2, 3).mapToUseCaseResult { it * 10 }.test {
            assertEquals(UseCaseResult.Success(10), awaitItem())
            assertEquals(UseCaseResult.Success(20), awaitItem())
            assertEquals(UseCaseResult.Success(30), awaitItem())
            awaitComplete()
        }
    }
}
