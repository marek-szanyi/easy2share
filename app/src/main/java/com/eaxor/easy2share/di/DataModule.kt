/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.di

import com.eaxor.easy2share.data.local.OnboardingLocalDataSource
import com.eaxor.easy2share.data.local.OnboardingPreferencesDataSource
import com.eaxor.easy2share.data.repository.OnboardingRepositoryImpl
import com.eaxor.easy2share.domain.repository.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the data-layer implementations to the abstractions the rest of the app
 * depends on, keeping the Clean Architecture dependency direction intact:
 * callers see only the domain [OnboardingRepository] / data-source interfaces,
 * never the concrete classes.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindOnboardingLocalDataSource(impl: OnboardingPreferencesDataSource): OnboardingLocalDataSource

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(impl: OnboardingRepositoryImpl): OnboardingRepository
}
