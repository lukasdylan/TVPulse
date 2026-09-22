package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTvShowByIdUseCaseTest {

    private val repository: TvShowRepository = mockk()
    private val useCase = GetTvShowByIdUseCase(repository = repository)

    @Test
    fun `returns Success with the show detail fetched by id`() = runTest {
        val detail = TvShowDetail(
            id = 1,
            name = "Breaking Bad",
            imageUrl = "url",
            genres = listOf("Drama"),
            rating = 9.0,
            synopsis = "synopsis"
        )
        coEvery { repository.fetchTvShowDetail(id = 1) } returns Result.success(detail)

        val result = useCase(input = 1)

        assertEquals(UseCaseResult.Success(detail), result)
    }

    @Test
    fun `returns GeneralError when the repository fails with an unexpected exception`() = runTest {
        coEvery { repository.fetchTvShowDetail(id = 1) } returns Result.failure(RuntimeException("boom"))

        val result = useCase(input = 1)

        assertEquals(UseCaseResult.GeneralError("Unexpected error occurred. Please try again."), result)
    }
}
