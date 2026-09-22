package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchTvShowsUseCaseTest {

    private val repository: TvShowRepository = mockk()
    private val useCase = SearchTvShowsUseCase(repository = repository)

    @Test
    fun `returns Success with shows matching the search text`() = runTest {
        val show = TvShow(id = 1, name = "Breaking Bad", imageUrl = "url", genres = listOf("Drama"), rating = 9.0)
        coEvery { repository.searchTvShowsByText(text = "bad") } returns Result.success(listOf(show))

        val result = useCase(input = "bad")

        assertEquals(UseCaseResult.Success(listOf(show)), result)
    }

    @Test
    fun `returns GeneralError when the repository fails with an unexpected exception`() = runTest {
        coEvery { repository.searchTvShowsByText(text = "bad") } returns Result.failure(RuntimeException("boom"))

        val result = useCase(input = "bad")

        assertEquals(UseCaseResult.GeneralError("Unexpected error occurred. Please try again."), result)
    }
}
