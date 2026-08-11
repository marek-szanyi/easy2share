/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaxor.easy2share.domain.usecase.ObserveOnboardingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Immutable UI state for the app-level shell.
 */
data class MainUiState(
    val isLoading: Boolean = true,
    val onboardingCompleted: Boolean = false,
)

/**
 * App-level ViewModel that decides which top-level destination is shown.
 *
 * It reactively mirrors the onboarding status coming from the domain layer, so
 * the moment onboarding is completed, the shell swaps to the home surface.
 */
@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        observeOnboardingStatusUseCase: ObserveOnboardingStatusUseCase,
    ) : ViewModel() {
        val uiState: StateFlow<MainUiState> =
            observeOnboardingStatusUseCase()
                .map { completed -> MainUiState(isLoading = false, onboardingCompleted = completed) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue = MainUiState(),
                )

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
