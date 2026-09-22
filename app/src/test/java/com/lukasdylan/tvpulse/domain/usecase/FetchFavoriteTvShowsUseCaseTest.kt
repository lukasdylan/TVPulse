package com.lukasdylan.tvpulse.domain.usecase

import app.cash.turbine.test
import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FetchFavoriteTvShowsUseCaseTest {

    private val repository: TvShowRepository = mockk()
    private val useCase = FetchFavoriteTvShowsUseCase(repository = repository)

    @Test
    fun `emits Success for every list of favorites observed from the repository`() = runTest {
        val show = TvShow(id = 1, name = "Breaking Bad", imageUrl = "url", genres = listOf("Drama"), rating = 9.0)
        every { repository.observeFavoriteTvShows() } returns flowOf(listOf(show), emptyList())

        useCase().test {
            assertEquals(UseCaseResult.Success(listOf(show)), awaitItem())
            assertEquals(UseCaseResult.Success(emptyList<TvShow>()), awaitItem())
            awaitComplete()
        }
    }
}
