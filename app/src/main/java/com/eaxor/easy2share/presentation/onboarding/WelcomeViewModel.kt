/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.onboarding

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaxor.easy2share.R
import com.eaxor.easy2share.domain.usecase.CompleteOnboardingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Names the illustration that heads a welcome page.
 *
 * The ViewModel layer only *identifies* the artwork; how it is rendered
 * (Compose canvases, accent colours, animation) is a View concern resolved in
 * the screen. This keeps the ViewModel free of UI-toolkit types.
 */
enum class WelcomeIllustration {
    FILE_PRESS,
    ENCRYPTION_GATE,
}

/**
 * Immutable description of one onboarding page: its copy and the identifier of
 * the illustration that heads it. Pure presentation data (no Compose types),
 * so the ViewModel can own it.
 */
data class WelcomePage(
    @StringRes val kicker: Int,
    @StringRes val title: Int,
    @StringRes val body: Int,
    @StringRes val frameTitle: Int,
    val illustration: WelcomeIllustration,
)

/**
 * Immutable UI state for the welcome / onboarding flow.
 */
data class WelcomeUiState(
    val pages: List<WelcomePage> = emptyList(),
    val currentPageIndex: Int = 0,
) {
    /** True when the user is viewing the final page of the story. */
    val isLastPage: Boolean
        get() = pages.isNotEmpty() && currentPageIndex == pages.lastIndex
}

/**
 * The onboarding story shown on the first run. Owned by the ViewModel layer, so the
 * View never decides *what* content is presented, only how to draw it.
 */
internal val welcomePages: List<WelcomePage> =
    listOf(
        WelcomePage(
            kicker = R.string.welcome_1_kicker,
            title = R.string.welcome_1_title,
            body = R.string.welcome_1_body,
            frameTitle = R.string.what,
            illustration = WelcomeIllustration.FILE_PRESS,
        ),
        WelcomePage(
            kicker = R.string.welcome_2_kicker,
            title = R.string.welcome_2_title,
            body = R.string.welcome_2_body,
            frameTitle = R.string.how,
            illustration = WelcomeIllustration.ENCRYPTION_GATE,
        ),
    )

/**
 * ViewModel for the welcome / onboarding flow.
 *
 * Owns the [WelcomeUiState] (the onboarding pages and the user's logical
 * position in the story) and exposes two intents: [onPageShown] to keep the
 * state in sync with the pager, and [completeOnboarding] when the user skips
 * or finishes. Persistence is delegated to the domain
 * [CompleteOnboardingUseCase]; the ViewModel only bridges UI events to the
 * domain and manages the coroutine scope.
 */
@HiltViewModel
class WelcomeViewModel
    @Inject
    constructor(
        private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(WelcomeUiState(pages = welcomePages))
        val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

        /** Invoked when the pager settles on [pageIndex], mirroring it into state. */
        fun onPageShown(pageIndex: Int) {
            _uiState.update { state ->
                if (state.pages.isEmpty()) {
                    state
                } else {
                    state.copy(currentPageIndex = pageIndex.coerceIn(0, state.pages.lastIndex))
                }
            }
        }

        /** Invoked when the user skips or reaches the end of the welcome flow. */
        fun completeOnboarding() {
            viewModelScope.launch {
                completeOnboardingUseCase()
            }
        }
    }
