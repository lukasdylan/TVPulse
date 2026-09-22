package com.lukasdylan.tvpulse.presentation.home

import app.cash.turbine.test
import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.usecase.FetchTvShowsUseCase
import com.lukasdylan.tvpulse.domain.usecase.NoParam
import com.lukasdylan.tvpulse.domain.usecase.SearchTvShowsUseCase
import com.lukasdylan.tvpulse.domain.usecase.UseCaseResult
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent
import com.lukasdylan.tvpulse.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fetchTvShowsUseCase: FetchTvShowsUseCase = mockk()
    private val searchTvShowsUseCase: SearchTvShowsUseCase = mockk()

    private val show = TvShow(id = 1, name = "Breaking Bad", imageUrl = "url", genres = listOf("Drama"), rating = 9.0)

    private fun createViewModel(): HomeViewModel {
        coEvery { fetchTvShowsUseCase.invoke(input = NoParam) } returns UseCaseResult.Success(listOf(show))
        return HomeViewModel(
            fetchTvShowsUseCase = fetchTvShowsUseCase,
            searchTvShowsUseCase = searchTvShowsUseCase
        )
    }

    @Test
    fun `loads tv shows on init and exposes them as Success`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            assertEquals(HomeUiState.Success(data = listOf(show), searchText = ""), awaitItem())
        }
    }

    @Test
    fun `loadTvShows emits Empty state when the result list is empty`() = runTest {
        coEvery { fetchTvShowsUseCase.invoke(input = NoParam) } returns UseCaseResult.Success(emptyList())
        val viewModel = HomeViewModel(
            fetchTvShowsUseCase = fetchTvShowsUseCase,
            searchTvShowsUseCase = searchTvShowsUseCase
        )

        viewModel.state.test {
            assertEquals(HomeUiState.Empty(searchText = ""), awaitItem())
        }
    }

    @Test
    fun `loadTvShows emits Error state and a connection dialog event on connection failure`() = runTest {
        coEvery { fetchTvShowsUseCase.invoke(input = NoParam) } returns UseCaseResult.ConnectionError
        val viewModel = HomeViewModel(
            fetchTvShowsUseCase = fetchTvShowsUseCase,
            searchTvShowsUseCase = searchTvShowsUseCase
        )

        viewModel.event.test {
            assertEquals(UiEvent.Dialog.ConnectionErrorDialog, awaitItem())
        }
        assertEquals(HomeUiState.Error(message = "", searchText = ""), viewModel.state.value)
    }

    @Test
    fun `loadTvShows emits Error state and a general error dialog event on general failure`() = runTest {
        coEvery { fetchTvShowsUseCase.invoke(input = NoParam) } returns UseCaseResult.GeneralError("boom")
        val viewModel = HomeViewModel(
            fetchTvShowsUseCase = fetchTvShowsUseCase,
            searchTvShowsUseCase = searchTvShowsUseCase
        )

        viewModel.event.test {
            assertEquals(UiEvent.Dialog.GeneralErrorDialog(errorReason = "boom"), awaitItem())
        }
        assertEquals(HomeUiState.Error(message = "", searchText = ""), viewModel.state.value)
    }

    @Test
    fun `inputSearchTvShow with blank text reloads all tv shows after the debounce`() = runTest {
        val viewModel = createViewModel()
        viewModel.state.test { awaitItem() }

        viewModel.inputSearchTvShow(" ")
        advanceUntilIdle()

        viewModel.state.test {
            assertEquals(HomeUiState.Success(data = listOf(show), searchText = ""), awaitItem())
        }
    }

    @Test
    fun `inputSearchTvShow with text searches tv shows after the debounce`() = runTest {
        val viewModel = createViewModel()
        viewModel.state.test { awaitItem() }
        val searchResult = TvShow(id = 2, name = "Better Call Saul", imageUrl = "url", genres = listOf("Drama"), rating = 8.5)
        coEvery { searchTvShowsUseCase(input = "saul") } returns UseCaseResult.Success(listOf(searchResult))

        viewModel.inputSearchTvShow("saul")
        advanceUntilIdle()

        viewModel.state.test {
            assertEquals(HomeUiState.Success(data = listOf(searchResult), searchText = "saul"), awaitItem())
        }
    }
}
