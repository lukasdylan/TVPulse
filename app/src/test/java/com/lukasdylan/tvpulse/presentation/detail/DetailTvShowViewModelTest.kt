package com.lukasdylan.tvpulse.presentation.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import com.lukasdylan.tvpulse.domain.usecase.AddFavoriteTvShowUseCase
import com.lukasdylan.tvpulse.domain.usecase.GetTvShowByIdUseCase
import com.lukasdylan.tvpulse.domain.usecase.ObserveFavoriteStateUseCase
import com.lukasdylan.tvpulse.domain.usecase.RemoveFavoriteTvShowUseCase
import com.lukasdylan.tvpulse.domain.usecase.UseCaseResult
import com.lukasdylan.tvpulse.presentation.Screen
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent
import com.lukasdylan.tvpulse.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailTvShowViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getTvShowByIdUseCase: GetTvShowByIdUseCase = mockk()
    private val addFavoriteTvShowUseCase: AddFavoriteTvShowUseCase = mockk()
    private val removeFavoriteTvShowUseCase: RemoveFavoriteTvShowUseCase = mockk()
    private val observeFavoriteStateUseCase: ObserveFavoriteStateUseCase = mockk()

    private val detail = TvShowDetail(
        id = 1,
        name = "Breaking Bad",
        imageUrl = "url",
        genres = listOf("Drama"),
        rating = 9.0,
        synopsis = "synopsis"
    )

    @Before
    fun setUp() {
        coEvery { observeFavoriteStateUseCase.invoke(input = 1) } returns flowOf(UseCaseResult.Success(false))
    }

    private fun createViewModel(showId: Int = 1): DetailTvShowViewModel = DetailTvShowViewModel(
        savedStateHandle = SavedStateHandle(mapOf(Screen.Detail.ARG_SHOW_ID to showId)),
        getTvShowByIdUseCase = getTvShowByIdUseCase,
        addFavoriteTvShowUseCase = addFavoriteTvShowUseCase,
        removeFavoriteTvShowUseCase = removeFavoriteTvShowUseCase,
        observeFavoriteStateUseCase = observeFavoriteStateUseCase
    )

    @Test
    fun `loads the tv show detail on init and exposes it as DetailShown`() = runTest {
        coEvery { getTvShowByIdUseCase(input = 1) } returns UseCaseResult.Success(detail)

        val viewModel = createViewModel()

        viewModel.state.test {
            assertEquals(DetailTvShowUiState.DetailShown(detail = detail, isFavorite = false), awaitItem())
        }
    }

    @Test
    fun `loadTvShowDetail sends a connection dialog event on connection failure`() = runTest {
        coEvery { getTvShowByIdUseCase(input = 1) } returns UseCaseResult.ConnectionError

        val viewModel = createViewModel()

        viewModel.event.test {
            assertEquals(UiEvent.Dialog.ConnectionErrorDialog, awaitItem())
        }
    }

    @Test
    fun `loadTvShowDetail moves to NotFound and sends a general error dialog on failure`() = runTest {
        coEvery { getTvShowByIdUseCase(input = 1) } returns UseCaseResult.GeneralError("boom")

        val viewModel = createViewModel()

        viewModel.event.test {
            assertEquals(UiEvent.Dialog.GeneralErrorDialog(errorReason = "boom"), awaitItem())
        }
        assertEquals(DetailTvShowUiState.NotFound(isFavorite = false), viewModel.state.value)
    }

    @Test
    fun `addToFavorite adds the currently shown detail and sends a snackbar event`() = runTest {
        coEvery { getTvShowByIdUseCase(input = 1) } returns UseCaseResult.Success(detail)
        coEvery { addFavoriteTvShowUseCase(input = detail) } returns UseCaseResult.Success(Unit)
        val viewModel = createViewModel()

        viewModel.event.test {
            viewModel.addToFavorite()
            assertEquals(UiEvent.SnackBar("Successfully add to favorite list"), awaitItem())
        }
        coVerify { addFavoriteTvShowUseCase(input = detail) }
    }

    @Test
    fun `removeFromFavorite removes the show by id and sends a snackbar event`() = runTest {
        coEvery { getTvShowByIdUseCase(input = 1) } returns UseCaseResult.Success(detail)
        coEvery { removeFavoriteTvShowUseCase(input = 1) } returns UseCaseResult.Success(Unit)
        val viewModel = createViewModel()

        viewModel.event.test {
            viewModel.removeFromFavorite()
            assertEquals(UiEvent.SnackBar("Successfully remove from favorite list"), awaitItem())
        }
        coVerify { removeFavoriteTvShowUseCase(input = 1) }
    }
}
