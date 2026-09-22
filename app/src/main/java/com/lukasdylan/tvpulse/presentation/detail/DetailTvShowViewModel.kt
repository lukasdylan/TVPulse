package com.lukasdylan.tvpulse.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import com.lukasdylan.tvpulse.domain.usecase.AddFavoriteTvShowUseCase
import com.lukasdylan.tvpulse.domain.usecase.GetTvShowByIdUseCase
import com.lukasdylan.tvpulse.domain.usecase.ObserveFavoriteStateUseCase
import com.lukasdylan.tvpulse.domain.usecase.RemoveFavoriteTvShowUseCase
import com.lukasdylan.tvpulse.domain.usecase.UseCaseResult
import com.lukasdylan.tvpulse.presentation.Screen
import com.lukasdylan.tvpulse.presentation.base.viewmodel.BaseViewModel
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent.Dialog.ConnectionErrorDialog
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent.Dialog.GeneralErrorDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTvShowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTvShowByIdUseCase: GetTvShowByIdUseCase,
    private val addFavoriteTvShowUseCase: AddFavoriteTvShowUseCase,
    private val removeFavoriteTvShowUseCase: RemoveFavoriteTvShowUseCase,
    private val observeFavoriteStateUseCase: ObserveFavoriteStateUseCase
) : BaseViewModel<DetailTvShowUiState>(initialState = DetailTvShowUiState.Loading()) {

    private val tvShowId: Int = runCatching { savedStateHandle[Screen.Detail.ARG_SHOW_ID] as Int? ?: 0 }.getOrDefault(0)

    init {
        viewModelScope.launch {
            observeFavoriteStateUseCase(input = tvShowId)
                .collectLatest {
                    if (it is UseCaseResult.Success) {
                        setState { updateFavoriteState(it.data) }
                    } else {
                        setState { updateFavoriteState(false) }
                    }
                }
        }
        loadTvShowDetail()
    }

    fun addToFavorite() {
        viewModelScope.launch {
            val tvShowDetail =
                (currentState as? DetailTvShowUiState.DetailShown)?.detail ?: return@launch
            addFavoriteTvShowUseCase(input = tvShowDetail)
            sendEvent(UiEvent.SnackBar("Successfully add to favorite list"))
        }
    }

    fun removeFromFavorite() {
        viewModelScope.launch {
            removeFavoriteTvShowUseCase(input = tvShowId)
            sendEvent(UiEvent.SnackBar("Successfully remove from favorite list"))
        }
    }

    fun loadTvShowDetail() {
        viewModelScope.launch {
            setState { DetailTvShowUiState.Loading() }
            when (val result = getTvShowByIdUseCase(input = tvShowId)) {
                UseCaseResult.ConnectionError -> sendEvent(ConnectionErrorDialog)
                is UseCaseResult.GeneralError -> {
                    sendEvent(
                        GeneralErrorDialog(
                            errorReason = result.errorReason
                        )
                    )
                    setState { DetailTvShowUiState.NotFound() }
                }

                is UseCaseResult.Success<TvShowDetail> -> {
                    setState {
                        DetailTvShowUiState.DetailShown(
                            detail = result.data,
                            isFavorite = currentState.isFavorite
                        )
                    }
                }
            }
        }
    }
}
