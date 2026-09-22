package com.lukasdylan.tvpulse.presentation.home

import androidx.lifecycle.viewModelScope
import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.usecase.FetchTvShowsUseCase
import com.lukasdylan.tvpulse.domain.usecase.SearchTvShowsUseCase
import com.lukasdylan.tvpulse.domain.usecase.UseCaseResult
import com.lukasdylan.tvpulse.domain.usecase.invoke
import com.lukasdylan.tvpulse.presentation.base.viewmodel.BaseViewModel
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent.Dialog.ConnectionErrorDialog
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent.Dialog.GeneralErrorDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchTvShowsUseCase: FetchTvShowsUseCase,
    private val searchTvShowsUseCase: SearchTvShowsUseCase
) : BaseViewModel<HomeUiState>(initialState = HomeUiState.Loading()) {

    private val tvShowSearchText = MutableStateFlow("")

    init {
        viewModelScope.launch {
            tvShowSearchText
                .onEach { setState { updateSearchText(it) } }
                .debounce(400.milliseconds)
                .distinctUntilChanged()
                .collectLatest {
                    if (it.isBlank()) {
                        loadTvShows()
                    } else {
                        searchTvShow(it)
                    }
                }
        }
        loadTvShows()
    }

    fun loadTvShows() {
        viewModelScope.launch {
            setState { HomeUiState.Loading() }
            when (val result = fetchTvShowsUseCase.invoke()) {
                UseCaseResult.ConnectionError -> {
                    sendEvent(ConnectionErrorDialog)
                    setState { HomeUiState.Error(message = "", searchText = searchText) }
                }
                is UseCaseResult.GeneralError -> {
                    sendEvent(
                        GeneralErrorDialog(
                            errorReason = result.errorReason
                        )
                    )
                    setState { HomeUiState.Error(message = "", searchText = searchText) }
                }

                is UseCaseResult.Success<List<TvShow>> -> {
                    if (result.data.isEmpty()) {
                        setState { HomeUiState.Empty(searchText = searchText) }
                    } else {
                        setState {
                            HomeUiState.Success(
                                data = result.data,
                                searchText = searchText
                            )
                        }
                    }
                }
            }
        }
    }

    fun inputSearchTvShow(text: String) {
        tvShowSearchText.value = text
    }

    private fun searchTvShow(text: String) {
        viewModelScope.launch {
            setState { HomeUiState.Loading(searchText = text) }
            when(val result = searchTvShowsUseCase(input = text)) {
                UseCaseResult.ConnectionError -> sendEvent(ConnectionErrorDialog)
                is UseCaseResult.GeneralError -> {
                    sendEvent(
                        GeneralErrorDialog(
                            errorReason = result.errorReason
                        )
                    )
                    setState { HomeUiState.Error(message = "", searchText = searchText) }
                }

                is UseCaseResult.Success<List<TvShow>> -> {
                    if (result.data.isEmpty()) {
                        setState { HomeUiState.Empty(searchText = searchText) }
                    } else {
                        setState {
                            HomeUiState.Success(
                                data = result.data,
                                searchText = searchText
                            )
                        }
                    }
                }
            }
        }
    }
}