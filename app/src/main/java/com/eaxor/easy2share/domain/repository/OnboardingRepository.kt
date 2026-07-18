package com.eaxor.easy2share.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Domain-level contract for onboarding persistence.
 *
 * The domain owns this abstraction; the data layer provides the concrete
 * implementation. This keeps the direction of dependency pointing inward
 * (data -> domain), as required by Clean Architecture.
 */
interface OnboardingRepository {

    /** Emits `true` once the user has completed (or skipped) the welcome flow. */
    val isOnboardingCompleted: Flow<Boolean>

    /** Persists the onboarding completion state. */
    suspend fun setOnboardingCompleted(completed: Boolean)
}

