package com.lukasdylan.tvpulse.presentation.base.viewmodel

sealed class UiEvent {
    data class Navigation(val path: String) : UiEvent()
    sealed class Dialog : UiEvent() {
        data object ConnectionErrorDialog : Dialog()
        data class GeneralErrorDialog(val errorReason: String) : Dialog()
    }

    data class SnackBar(val message: String) : UiEvent()
}