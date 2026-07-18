package com.eaxor.easy2share.presentation.home

import androidx.lifecycle.ViewModel
import com.eaxor.easy2share.domain.usecase.GetGreetingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Immutable UI state for the home surface.
 */
data class HomeUiState(
    val greeting: String = "",
)

/**
 * ViewModel for the home surface.
 *
 * Pulls the [com.eaxor.easy2share.domain.model.Greeting] from the domain layer
 * and maps it into a display-ready [HomeUiState]. Formatting is a presentation
 * concern and therefore lives here rather than in the use case.
 */
class HomeViewModel(
    getGreetingUseCase: GetGreetingUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val greeting = getGreetingUseCase()
        _uiState.value = HomeUiState(greeting = "Hello ${greeting.recipient}!")
    }
}

