package com.eaxor.easy2share.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eaxor.easy2share.domain.usecase.CompleteOnboardingUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel for the welcome / onboarding flow.
 *
 * Exposes a single [completeOnboarding] intent. The actual persistence is
 * delegated to the domain [CompleteOnboardingUseCase]; the ViewModel only
 * bridges UI events to the domain and manages the coroutine scope.
 */
class WelcomeViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) : ViewModel() {

    /** Invoked when the user skips or reaches the end of the welcome flow. */
    fun completeOnboarding() {
        viewModelScope.launch {
            completeOnboardingUseCase()
        }
    }
}

