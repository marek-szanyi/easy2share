package com.eaxor.easy2share.domain.usecase

import com.eaxor.easy2share.domain.repository.OnboardingRepository
import javax.inject.Inject

/**
 * Marks the onboarding flow as completed.
 *
 * Encapsulates the business rule that "finishing" onboarding means persisting a
 * completed state, hiding that detail from the presentation layer.
 */
class CompleteOnboardingUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) {
    suspend operator fun invoke() = onboardingRepository.setOnboardingCompleted(true)
}

