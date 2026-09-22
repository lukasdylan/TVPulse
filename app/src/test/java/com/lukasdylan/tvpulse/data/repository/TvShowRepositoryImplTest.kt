package com.lukasdylan.tvpulse.data.repository

import app.cash.turbine.test
import com.lukasdylan.tvpulse.data.remote.model.SearchTvShowResponseBody
import com.lukasdylan.tvpulse.data.remote.model.TvShowImageResponseBody
import com.lukasdylan.tvpulse.data.remote.model.TvShowRatingResponseBody
import com.lukasdylan.tvpulse.data.remote.model.TvShowResponseBody
import com.lukasdylan.tvpulse.data.remote.service.TvMazeApiService
import com.lukasdylan.tvpulse.data.storage.dao.FavoriteTvShowDao
import com.lukasdylan.tvpulse.data.storage.entity.FavoriteTvShowEntity
import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TvShowRepositoryImplTest {

    private val apiService: TvMazeApiService = mockk()
    private val dao: FavoriteTvShowDao = mockk()
    private lateinit var repository: TvShowRepositoryImpl

    @Before
    fun setUp() {
        repository = TvShowRepositoryImpl(apiService = apiService, dao = dao)
    }

    private fun tvShowResponse(
        id: Int? = 1,
        name: String? = "Breaking Bad",
        summary: String? = "A show",
        genres: List<String>? = listOf("Drama"),
        image: TvShowImageResponseBody? = TvShowImageResponseBody(medium = "medium.png", original = "original.png"),
        rating: TvShowRatingResponseBody? = TvShowRatingResponseBody(average = 9.5)
    ) = TvShowResponseBody(
        id = id,
        name = name,
        summary = summary,
        genres = genres,
        image = image,
        rating = rating
    )

    @Test
    fun `fetchTvShows maps response body to domain model`() = runTest {
        coEvery { apiService.getTvShows() } returns listOf(tvShowResponse())

        val result = repository.fetchTvShows()

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(
                TvShow(
                    id = 1,
                    name = "Breaking Bad",
                    imageUrl = "medium.png",
                    genres = listOf("Drama"),
                    rating = 9.5
                )
            ),
            result.getOrNull()
        )
    }

    @Test
    fun `fetchTvShows limits result to the first 30 shows`() = runTest {
        val shows = (1..40).map { tvShowResponse(id = it, name = "Show $it") }
        coEvery { apiService.getTvShows() } returns shows

        val result = repository.fetchTvShows().getOrThrow()

        assertEquals(30, result.size)
        assertEquals(1, result.first().id)
        assertEquals(30, result.last().id)
    }

    @Test
    fun `fetchTvShows falls back to defaults for null fields`() = runTest {
        coEvery { apiService.getTvShows() } returns listOf(
            tvShowResponse(id = null, name = null, genres = null, image = null, rating = null)
        )

        val result = repository.fetchTvShows().getOrThrow()

        assertEquals(
            TvShow(id = 0, name = "", imageUrl = "", genres = emptyList(), rating = 0.0),
            result.single()
        )
    }

    @Test
    fun `fetchTvShows falls back to original image when medium is missing`() = runTest {
        coEvery { apiService.getTvShows() } returns listOf(
            tvShowResponse(image = TvShowImageResponseBody(medium = null, original = "original.png"))
        )

        val result = repository.fetchTvShows().getOrThrow()

        assertEquals("original.png", result.single().imageUrl)
    }

    @Test
    fun `fetchTvShows wraps api exceptions as a failed Result`() = runTest {
        val exception = IllegalStateException("network down")
        coEvery { apiService.getTvShows() } throws exception

        val result = repository.fetchTvShows()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `searchTvShowsByText maps response body to domain model`() = runTest {
        coEvery { apiService.searchTvShows(query = "bad") } returns listOf(
            SearchTvShowResponseBody(show = tvShowResponse())
        )

        val result = repository.searchTvShowsByText("bad")

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(
                TvShow(
                    id = 1,
                    name = "Breaking Bad",
                    imageUrl = "medium.png",
                    genres = listOf("Drama"),
                    rating = 9.5
                )
            ),
            result.getOrNull()
        )
    }

    @Test
    fun `searchTvShowsByText falls back to defaults when show is null`() = runTest {
        coEvery { apiService.searchTvShows(query = "bad") } returns listOf(
            SearchTvShowResponseBody(show = null)
        )

        val result = repository.searchTvShowsByText("bad").getOrThrow()

        assertEquals(
            TvShow(id = 0, name = "", imageUrl = "", genres = emptyList(), rating = 0.0),
            result.single()
        )
    }

    @Test
    fun `searchTvShowsByText wraps api exceptions as a failed Result`() = runTest {
        val exception = IllegalStateException("network down")
        coEvery { apiService.searchTvShows(query = "bad") } throws exception

        val result = repository.searchTvShowsByText("bad")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `fetchTvShowDetail maps response body to domain model`() = runTest {
        coEvery { apiService.getTvShowDetail(id = 1) } returns tvShowResponse(summary = "Full synopsis")

        val result = repository.fetchTvShowDetail(id = 1)

        assertTrue(result.isSuccess)
        assertEquals(
            TvShowDetail(
                id = 1,
                name = "Breaking Bad",
                imageUrl = "medium.png",
                genres = listOf("Drama"),
                rating = 9.5,
                synopsis = "Full synopsis"
            ),
            result.getOrNull()
        )
    }

    @Test
    fun `fetchTvShowDetail wraps api exceptions as a failed Result`() = runTest {
        val exception = IllegalStateException("not found")
        coEvery { apiService.getTvShowDetail(id = 1) } throws exception

        val result = repository.fetchTvShowDetail(id = 1)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `observeFavoriteTvShows maps entities emitted by the dao`() = runTest {
        every { dao.observeFavoriteTvShows() } returns flowOf(
            listOf(
                FavoriteTvShowEntity(
                    id = 1,
                    name = "Breaking Bad",
                    imageUrl = "medium.png",
                    genres = listOf("Drama"),
                    rating = 9.5,
                    createdDate = 123L
                )
            )
        )

        repository.observeFavoriteTvShows().test {
            assertEquals(
                listOf(
                    TvShow(
                        id = 1,
                        name = "Breaking Bad",
                        imageUrl = "medium.png",
                        genres = listOf("Drama"),
                        rating = 9.5
                    )
                ),
                awaitItem()
            )
            awaitComplete()
        }
    }

    @Test
    fun `observeIsFavorite delegates to the dao`() = runTest {
        every { dao.observeIsFavorite(id = 5) } returns flowOf(true)

        repository.observeIsFavorite(id = 5).test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `addFavorite inserts a mapped entity with the current timestamp`() = runTest {
        val entitySlot = slot<FavoriteTvShowEntity>()
        coEvery { dao.insert(entity = capture(entitySlot)) } returns Unit
        val detail = TvShowDetail(
            id = 1,
            name = "Breaking Bad",
            imageUrl = "medium.png",
            genres = listOf("Drama"),
            rating = 9.5,
            synopsis = "synopsis"
        )

        repository.addFavorite(show = detail)

        coVerify { dao.insert(entity = any()) }
        assertEquals(1, entitySlot.captured.id)
        assertEquals("Breaking Bad", entitySlot.captured.name)
        assertEquals("medium.png", entitySlot.captured.imageUrl)
        assertEquals(listOf("Drama"), entitySlot.captured.genres)
        assertEquals(9.5, entitySlot.captured.rating, 0.0)
    }

    @Test
    fun `addFavorite swallows exceptions thrown by the dao`() = runTest {
        coEvery { dao.insert(entity = any()) } throws RuntimeException("db error")
        val detail = TvShowDetail(
            id = 1,
            name = "Breaking Bad",
            imageUrl = "medium.png",
            genres = listOf("Drama"),
            rating = 9.5,
            synopsis = "synopsis"
        )

        repository.addFavorite(show = detail)

        coVerify { dao.insert(entity = any()) }
    }

    @Test
    fun `removeFavorite deletes the show by id`() = runTest {
        coEvery { dao.deleteById(id = 1) } returns Unit

        repository.removeFavorite(id = 1)

        coVerify { dao.deleteById(id = 1) }
    }

    @Test
    fun `removeFavorite swallows exceptions thrown by the dao`() = runTest {
        coEvery { dao.deleteById(id = 1) } throws RuntimeException("db error")

        repository.removeFavorite(id = 1)

        coVerify { dao.deleteById(id = 1) }
    }
}
