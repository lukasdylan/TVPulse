package com.lukasdylan.tvpulse.presentation.favorite

import app.cash.turbine.test
import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.usecase.FetchFavoriteTvShowsUseCase
import com.lukasdylan.tvpulse.domain.usecase.NoParam
import com.lukasdylan.tvpulse.domain.usecase.RemoveFavoriteTvShowUseCase
import com.lukasdylan.tvpulse.domain.usecase.UseCaseResult
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent
import com.lukasdylan.tvpulse.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoriteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fetchFavoriteTvShowsUseCase: FetchFavoriteTvShowsUseCase = mockk()
    private val removeFavoriteTvShowUseCase: RemoveFavoriteTvShowUseCase = mockk()

    private val show = TvShow(id = 1, name = "Breaking Bad", imageUrl = "url", genres = listOf("Drama"), rating = 9.0)

    private fun createViewModel(): FavoriteViewModel = FavoriteViewModel(
        fetchFavoriteTvShowsUseCase = fetchFavoriteTvShowsUseCase,
        removeFavoriteTvShowUseCase = removeFavoriteTvShowUseCase
    )

    @Test
    fun `loads favorites on init and exposes them as Available`() = runTest {
        coEvery { fetchFavoriteTvShowsUseCase.invoke(input = NoParam) } returns flowOf(UseCaseResult.Success(listOf(show)))

        val viewModel = createViewModel()

        viewModel.state.test {
            assertEquals(FavoriteUiState.Available(listOf(show)), awaitItem())
        }
    }

    @Test
    fun `exposes Empty state when there are no favorites`() = runTest {
        coEvery { fetchFavoriteTvShowsUseCase.invoke(input = NoParam) } returns flowOf(UseCaseResult.Success(emptyList()))

        val viewModel = createViewModel()

        viewModel.state.test {
            assertEquals(FavoriteUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `sends a connection dialog event on connection failure`() = runTest {
        coEvery { fetchFavoriteTvShowsUseCase.invoke(input = NoParam) } returns flowOf(UseCaseResult.ConnectionError)

        val viewModel = createViewModel()

        viewModel.event.test {
            assertEquals(UiEvent.Dialog.ConnectionErrorDialog, awaitItem())
        }
    }

    @Test
    fun `sends a general error dialog event and resets to Empty on general failure`() = runTest {
        coEvery { fetchFavoriteTvShowsUseCase.invoke(input = NoParam) } returns flowOf(UseCaseResult.GeneralError("boom"))

        val viewModel = createViewModel()

        viewModel.event.test {
            assertEquals(UiEvent.Dialog.GeneralErrorDialog(errorReason = "boom"), awaitItem())
        }
        assertEquals(FavoriteUiState.Empty, viewModel.state.value)
    }

    @Test
    fun `removeFavorite delegates to the use case with the given id`() = runTest {
        coEvery { fetchFavoriteTvShowsUseCase.invoke(input = NoParam) } returns flowOf(UseCaseResult.Success(listOf(show)))
        coEvery { removeFavoriteTvShowUseCase(input = 1) } returns UseCaseResult.Success(Unit)
        val viewModel = createViewModel()

        viewModel.removeFavorite(id = 1)

        coVerify { removeFavoriteTvShowUseCase(input = 1) }
    }
}
