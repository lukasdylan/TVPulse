package com.lukasdylan.tvpulse.presentation.detail

import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiState

sealed class DetailTvShowUiState : UiState {
    abstract val isFavorite: Boolean

    abstract fun updateFavoriteState(isFavorite: Boolean): DetailTvShowUiState

    data class Loading(override val isFavorite: Boolean = false) : DetailTvShowUiState() {
        override fun updateFavoriteState(isFavorite: Boolean): DetailTvShowUiState =
            copy(isFavorite = isFavorite)
    }

    data class DetailShown(val detail: TvShowDetail, override val isFavorite: Boolean) :
        DetailTvShowUiState() {
        override fun updateFavoriteState(isFavorite: Boolean): DetailTvShowUiState =
            copy(isFavorite = isFavorite)
    }

    data class NotFound(override val isFavorite: Boolean = false) : DetailTvShowUiState() {
        override fun updateFavoriteState(isFavorite: Boolean): DetailTvShowUiState =
            copy(isFavorite = isFavorite)
    }
}