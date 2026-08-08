/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.domain.usecase

import com.eaxor.easy2share.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Observes whether the onboarding flow has been completed.
 *
 * A single-responsibility interactor exposed through an `operator fun invoke`
 * so callers can treat it as a function: `observeOnboardingStatusUseCase()`.
 */
class ObserveOnboardingStatusUseCase
    @Inject
    constructor(
        private val onboardingRepository: OnboardingRepository,
    ) {
        operator fun invoke(): Flow<Boolean> = onboardingRepository.isOnboardingCompleted
    }
