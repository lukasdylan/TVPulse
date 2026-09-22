package com.lukasdylan.tvpulse.presentation.home

import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiState

sealed class HomeUiState: UiState {
    abstract val searchText: String

    abstract fun updateSearchText(text: String): HomeUiState

    fun onSearchState(): Boolean = searchText.isNotBlank()

    data class Loading(override val searchText: String = "") : HomeUiState() {
        override fun updateSearchText(text: String): HomeUiState = copy(searchText = text)
    }

    data class Success(val data: List<TvShow>, override val searchText: String) : HomeUiState() {
        override fun updateSearchText(text: String): HomeUiState = copy(searchText = text)
    }

    data class Error(val message: String, override val searchText: String) : HomeUiState() {
        override fun updateSearchText(text: String): HomeUiState = copy(searchText = text)
    }

    data class Empty(override val searchText: String) : HomeUiState() {
        override fun updateSearchText(text: String): HomeUiState = copy(searchText = text)
    }
}
