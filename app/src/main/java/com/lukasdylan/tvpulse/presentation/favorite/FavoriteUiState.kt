package com.lukasdylan.tvpulse.presentation.favorite

import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiState

sealed class FavoriteUiState : UiState {
    data object Empty : FavoriteUiState()
    data class Available(val favorites: List<TvShow>) : FavoriteUiState()
}