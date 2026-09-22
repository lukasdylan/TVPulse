package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoveFavoriteTvShowUseCaseTest {

    private val repository: TvShowRepository = mockk()
    private val useCase = RemoveFavoriteTvShowUseCase(repository = repository)

    @Test
    fun `removes the show from favorites through the repository and returns Success`() = runTest {
        coEvery { repository.removeFavorite(id = 1) } returns Unit

        val result = useCase(input = 1)

        coVerify { repository.removeFavorite(id = 1) }
        assertEquals(UseCaseResult.Success(Unit), result)
    }
}
