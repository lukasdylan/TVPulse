package com.lukasdylan.tvpulse.presentation.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : UiState>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _event = Channel<UiEvent>(Channel.BUFFERED)
    val event: Flow<UiEvent> = _event.receiveAsFlow()

    protected val currentState: State
        get() = _state.value

    protected fun setState(reducer: State.() -> State) {
        _state.update(reducer)
    }

    protected fun sendEvent(event: UiEvent) {
        viewModelScope.launch { _event.send(event) }
    }
}
