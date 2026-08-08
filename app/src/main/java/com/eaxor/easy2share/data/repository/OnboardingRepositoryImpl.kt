/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.data.repository

import com.eaxor.easy2share.data.local.OnboardingLocalDataSource
import com.eaxor.easy2share.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Data-layer implementation of the domain's [OnboardingRepository].
 *
 * Delegates to an [OnboardingLocalDataSource]. This is where remote sources,
 * caching or mapping would be coordinated if the feature grew.
 */
class OnboardingRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: OnboardingLocalDataSource,
    ) : OnboardingRepository {
        override val isOnboardingCompleted: Flow<Boolean> =
            localDataSource.isOnboardingCompleted

        override suspend fun setOnboardingCompleted(completed: Boolean) = localDataSource.setOnboardingCompleted(completed)
    }
