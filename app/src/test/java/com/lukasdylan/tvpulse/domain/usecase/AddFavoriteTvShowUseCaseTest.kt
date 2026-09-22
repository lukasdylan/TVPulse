package com.lukasdylan.tvpulse.domain.usecase

import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import com.lukasdylan.tvpulse.domain.repository.TvShowRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AddFavoriteTvShowUseCaseTest {

    private val repository: TvShowRepository = mockk()
    private val useCase = AddFavoriteTvShowUseCase(repository = repository)

    @Test
    fun `adds the show to favorites through the repository and returns Success`() = runTest {
        val detail = TvShowDetail(
            id = 1,
            name = "Breaking Bad",
            imageUrl = "url",
            genres = listOf("Drama"),
            rating = 9.0,
            synopsis = "synopsis"
        )
        coEvery { repository.addFavorite(show = detail) } returns Unit

        val result = useCase(input = detail)

        coVerify { repository.addFavorite(show = detail) }
        assertEquals(UseCaseResult.Success(Unit), result)
    }
}
