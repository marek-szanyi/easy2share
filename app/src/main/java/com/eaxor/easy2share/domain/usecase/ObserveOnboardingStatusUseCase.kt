package com.eaxor.easy2share.domain.usecase

import com.eaxor.easy2share.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes whether the onboarding flow has been completed.
 *
 * A single-responsibility interactor exposed through an `operator fun invoke`
 * so callers can treat it as a function: `observeOnboardingStatusUseCase()`.
 */
class ObserveOnboardingStatusUseCase(
    private val onboardingRepository: OnboardingRepository,
) {
    operator fun invoke(): Flow<Boolean> = onboardingRepository.isOnboardingCompleted
}

