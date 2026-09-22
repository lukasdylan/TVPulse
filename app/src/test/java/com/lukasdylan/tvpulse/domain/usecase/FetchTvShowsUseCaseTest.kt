package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Test

class FetchTvShowsUseCaseTest {

    private val repository: TvShowRepository = mockk()
    private val useCase = FetchTvShowsUseCase(repository = repository)

    private val show = TvShow(id = 1, name = "Breaking Bad", imageUrl = "url", genres = listOf("Drama"), rating = 9.0)

    @Test
    fun `returns Success with shows fetched from the repository`() = runTest {
        coEvery { repository.fetchTvShows() } returns Result.success(listOf(show))

        val result = useCase()

        assertEquals(UseCaseResult.Success(listOf(show)), result)
    }

    @Test
    fun `returns ConnectionError when the repository fails with an IOException`() = runTest {
        coEvery { repository.fetchTvShows() } returns Result.failure(IOException())

        val result = useCase()

        assertEquals(UseCaseResult.ConnectionError, result)
    }
}
