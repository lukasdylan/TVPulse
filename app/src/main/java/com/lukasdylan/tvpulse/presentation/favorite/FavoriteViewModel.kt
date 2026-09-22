package com.lukasdylan.tvpulse.presentation.favorite

import androidx.lifecycle.viewModelScope
import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.domain.usecase.FetchFavoriteTvShowsUseCase
import com.lukasdylan.tvpulse.domain.usecase.RemoveFavoriteTvShowUseCase
import com.lukasdylan.tvpulse.domain.usecase.UseCaseResult
import com.lukasdylan.tvpulse.domain.usecase.invoke
import com.lukasdylan.tvpulse.presentation.base.viewmodel.BaseViewModel
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent.Dialog.ConnectionErrorDialog
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent.Dialog.GeneralErrorDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val fetchFavoriteTvShowsUseCase: FetchFavoriteTvShowsUseCase,
    private val removeFavoriteTvShowUseCase: RemoveFavoriteTvShowUseCase,
) : BaseViewModel<FavoriteUiState>(initialState = FavoriteUiState.Empty) {

    init {
        loadFavoriteTvShows()
    }

    fun loadFavoriteTvShows() {
        viewModelScope.launch {
            fetchFavoriteTvShowsUseCase().collectLatest {
                when (it) {
                    UseCaseResult.ConnectionError -> sendEvent(ConnectionErrorDialog)
                    is UseCaseResult.GeneralError -> {
                        sendEvent(
                            GeneralErrorDialog(
                                errorReason = it.errorReason
                            )
                        )
                        setState { FavoriteUiState.Empty }
                    }

                    is UseCaseResult.Success<List<TvShow>> -> {
                        if (it.data.isEmpty()) {
                            setState { FavoriteUiState.Empty }
                        } else {
                            setState { FavoriteUiState.Available(it.data) }
                        }
                    }
                }
            }
        }
    }

    fun removeFavorite(id: Int) {
        viewModelScope.launch {
            removeFavoriteTvShowUseCase(input = id)
        }
    }
}