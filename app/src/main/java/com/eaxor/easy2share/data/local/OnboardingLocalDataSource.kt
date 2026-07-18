package com.eaxor.easy2share.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over the on-device storage that backs onboarding state.
 *
 * Keeping this behind an interface lets the repository stay agnostic of the
 * storage technology (DataStore today, something else tomorrow) and makes the
 * data layer straightforward to fake in tests.
 */
interface OnboardingLocalDataSource {

    val isOnboardingCompleted: Flow<Boolean>

    suspend fun setOnboardingCompleted(completed: Boolean)
}

