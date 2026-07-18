package com.eaxor.easy2share.data.repository

import com.eaxor.easy2share.data.local.OnboardingLocalDataSource
import com.eaxor.easy2share.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Data-layer implementation of the domain's [OnboardingRepository].
 *
 * Delegates to an [OnboardingLocalDataSource]. This is where remote sources,
 * caching or mapping would be coordinated if the feature grew.
 */
class OnboardingRepositoryImpl(
    private val localDataSource: OnboardingLocalDataSource,
) : OnboardingRepository {

    override val isOnboardingCompleted: Flow<Boolean> =
        localDataSource.isOnboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) =
        localDataSource.setOnboardingCompleted(completed)
}

