package com.lukasdylan.tvpulse.domain.usecase

import app.cash.turbine.test
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveFavoriteStateUseCaseTest {

    private val repository: TvShowRepository = mockk()
    private val useCase = ObserveFavoriteStateUseCase(repository = repository)

    @Test
    fun `emits Success for every favorite state observed from the repository`() = runTest {
        every { repository.observeIsFavorite(id = 1) } returns flowOf(false, true)

        useCase(input = 1).test {
            assertEquals(UseCaseResult.Success(false), awaitItem())
            assertEquals(UseCaseResult.Success(true), awaitItem())
            awaitComplete()
        }
    }
}
